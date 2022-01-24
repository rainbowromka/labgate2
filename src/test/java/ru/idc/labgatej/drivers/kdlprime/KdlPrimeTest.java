package ru.idc.labgatej.drivers.kdlprime;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.idc.labgatej.Manager;
import ru.idc.labgatej.base.AppPooledDataSource;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.DriverContext;
import ru.idc.labgatej.base.protocols.ProtocolKDLPrimeASTM;
import ru.idc.labgatej.drivers.KDLPrime.KDLPrimeDriver;
import ru.idc.labgatej.drivers.KDLPrime.KdlPrime;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Manager.class, DBManager.class, KDLPrimeDriver.class, Codes.class, ProtocolKDLPrimeASTM.class})
@PowerMockIgnore({"com.mchange.v2.c3p0.ComboPooledDataSource",
    "org.apache.log4j.*"})
@Slf4j
public class KdlPrimeTest
{
    private static Configuration config = new Configuration();

    @Mock
    DBManager dbManager;
    @Mock
    ProtocolKDLPrimeASTM protocolKDLPrimeASTM;

    @Before
    public void init()
        throws Exception
    {
        PowerMockito.whenNew(DBManager.class).withAnyArguments()
            .thenReturn(dbManager);
        PowerMockito.whenNew(ProtocolKDLPrimeASTM.class).withAnyArguments()
            .thenReturn(protocolKDLPrimeASTM);
    }

    @Test
    public void testKdlDriverTimeout()
        throws Exception
    {
        // 1. Поднимаем сервер.
        KdlPrime server = new KdlPrime();
//        server.init(cpds, config);
        server.init(new DriverContext(null, config, new AtomicBoolean(true)));
        new Thread(() -> {
            try
            {
                server.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(250);

        Thread task1 = new Thread(() -> {
            runClientAndSendASTMData(new ImmutableList.Builder<String>()
                    .add("<STX>1H|\\^&||FRTManager|FRTManager|||||FRTManager||P||20210301170956<CR><ETX>BF<CR><LF>")
                    .add("<STX>2P|1||||^^|||M|||||||||||||||||||||||||<CR><ETX>C8<CR><LF>")
                    .add("<STX>3O|1|221032909211||^^^FluA_B_cfx||20210301133825|20210301||||||||3||||18,05,2019^20210703|||||||||||<CR><ETX>D1<CR><LF>")
                    .add("<STX>4R|1|^^^FluA_B_cfx^InfB|2|||||F||^Default (1)||||<CR><ETX>4C<CR><LF>")
                    .add("<STX>5R|2|^^^FluA_B_cfx^InfA|2|||||F||^Default (1)||||<CR><ETX>4D<CR><LF>")
                    .add("<STX>6L|1|N<CR><ETX>09<CR><LF>")
                    .build()
            );
        });

        Thread task2 = new Thread(() -> {
            runClientAndSendASTMData( new ImmutableList.Builder<String>()
                .add("<STX>1H|\\^&||FRTManager|FRTManager|||||FRTManager||P||20210301171404<CR><ETX>B4<CR><LF>")
                .add("<STX>2P|1||||^^|||M|||||||||||||||||||||||||<CR><ETX>C8<CR><LF>")
                .add("<STX>3O|1|2247550||^^^hAdv_hBov_cfx||20210301133949|20210301||||||||3||||18,06,2019^20211022|||||||||||<CR><ETX>3C<CR><LF>")
                .add("<STX>4R|1|^^^hAdv_hBov_cfx^hBov|2|||||F||^Default (1)||||<CR><ETX>E4<CR><LF>")
                .add("<STX>5R|2|^^^hAdv_hBov_cfx^hAdv|2|||||F||^Default (1)||||<CR><ETX>DA<CR><LF>")
                .add("<STX>6L|1|N<CR><ETX>09<CR><LF>")
                .build()
            );
        });

        task1.start();
        //task2.start();

//        Thread.sleep(1000*60*60);
//        Thread.sleep(3000);

        // 4. Тестируем отправленные данные.
        String rstStr1 = Codes.makeSendable("1H|\\^&||FRTManager|FRTManager|||||FRTManager||P||20210301170956<CR><ETX>BF<CR><LF>2P|1||||^^|||M|||||||||||||||||||||||||<CR><ETX>C8<CR><LF>3O|1|221032909211||^^^FluA_B_cfx||20210301133825|20210301||||||||3||||18,05,2019^20210703|||||||||||<CR><ETX>D1<CR><LF>4R|1|^^^FluA_B_cfx^InfB|2|||||F||^Default (1)||||<CR><ETX>4C<CR><LF>5R|2|^^^FluA_B_cfx^InfA|2|||||F||^Default (1)||||<CR><ETX>4D<CR><LF>6L|1|N<CR><ETX>09<CR><LF>");
        String rstStr2 = Codes.makeSendable("1H|\\^&||FRTManager|FRTManager|||||FRTManager||P||20210301171404<CR><ETX>B4<CR><LF>2P|1||||^^|||M|||||||||||||||||||||||||<CR><ETX>C8<CR><LF>3O|1|2247550||^^^hAdv_hBov_cfx||20210301133949|20210301||||||||3||||18,06,2019^20211022|||||||||||<CR><ETX>3C<CR><LF>4R|1|^^^hAdv_hBov_cfx^hBov|2|||||F||^Default (1)||||<CR><ETX>E4<CR><LF>5R|2|^^^hAdv_hBov_cfx^hAdv|2|||||F||^Default (1)||||<CR><ETX>DA<CR><LF>6L|1|N<CR><ETX>09<CR><LF>");

        Collection<Invocation> invocations = Mockito.mockingDetails(protocolKDLPrimeASTM).getInvocations();
        Long currentTime = new Date().getTime();
        Long lastTime = new Date().getTime();
        while (invocations.size() < 2 && lastTime - currentTime < 1000 )
        {
            //lastTime = new Date().getTime();
        };

        Mockito.verify(protocolKDLPrimeASTM)
            .parseMessage(rstStr1);
        Mockito.verify(protocolKDLPrimeASTM)
            .parseMessage(rstStr2);
    }

    @SneakyThrows
    private void runClientAndSendASTMData(ImmutableList<String> astmMsg) {
        ProtocolClient client = new ProtocolClient();
        client.startConnection("localhost",
            Integer.parseInt(config.getParamValue("kdlprime.port.result")));
        client.sendPacket(astmMsg);
        client.stopConnection();
    }
}
