package ru.idc.labgatej.drivers.kdlmaxdriver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.idc.labgatej.Manager;
import ru.idc.labgatej.base.TaskDualDriver;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.SocketClientTransport;
import ru.idc.labgatej.drivers.KdlMax.KdlMaxDriverAstmDual;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Manager.class, SocketClientTransport.class,
    KdlMaxDriverAstmDual.class, TaskDualDriver.class})
public class KDLMaxDriverTestGetTasks
{
    @Mock
    SocketClientTransport socketClientTransport;

    KdlMaxDriverAstmDual kdlMaxDriverAstm;

    @Before
    public void init()
    throws Exception
    {
        PowerMockito.whenNew(SocketClientTransport.class)
            .withAnyArguments()
            .thenReturn(socketClientTransport);

        Mockito.doNothing().when(socketClientTransport).init(Mockito.anyInt());

        Mockito.doNothing().when(socketClientTransport).sendMessage(Mockito.anyString());

        Mockito.when(socketClientTransport.isReady()).thenReturn(true);


    }

    @Test
    public void testDriver()
    throws Exception
    {
        Mockito.when(socketClientTransport.readInt())
                .thenReturn((int) Codes.ACK)
                .thenReturn((int) Codes.NAK);

        //PowerMockito.mockStatic(Manager.class);
        //Manager.runManager(new String[]{""});
        kdlMaxDriverAstm.sendTasks();
    }
}
