package ru.idc.labgatej.drivers.KDLPrime;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.ResultDriver;
import ru.idc.labgatej.base.SocketClientTransport;
import ru.idc.labgatej.base.protocols.ProtocolKDLPrimeASTM;
import ru.idc.labgatej.model.PacketInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

import static ru.idc.labgatej.base.Codes.ENQ;
import static ru.idc.labgatej.base.Codes.EOT;
import static ru.idc.labgatej.base.Codes.STX;
import static ru.idc.labgatej.base.Codes.makeSendable;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

/**
 * Драйвер KDL Prime, принимает результаты иследований с FRT Manager.
 */
@Slf4j
public class KDLPrimeDriver extends ResultDriver<ProtocolKDLPrimeASTM>
{

    /**
     * Адрес входящего соединения, удобно при логгировании.
     */
    private String inetAddress;

    public void init(ComboPooledDataSource connectionPool, Configuration config, Socket socket)
    {
        this.dbManager4results = new DBManager();
        dbManager4results.init(connectionPool);

        transport4results = new SocketClientTransport(null, 0);
        transport4results.init(socket, 10000);

        protocol = new ProtocolKDLPrimeASTM(config.getParamValue("code"));
        inetAddress = "["
            + socket.getInetAddress() + ":" + socket.getPort() + "] ";
    }

    @Override
    public void loop() throws IOException, InterruptedException, SQLException {
        String msg = null;

        msg = receiveResults();

        if (msg != null && !msg.isEmpty()) {
            PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
            dbManager4results.saveResults(packetInfo, false);
        }
        Thread.sleep(500);

        close();
    }

    protected String receiveResults() throws IOException {
        StringBuilder sb = new StringBuilder();
        int res;
        do {
            res = transport4results.readInt(true);
            if (res == ERROR_TIMEOUT) {
                log.trace(inetAddress + "ждём данных");
            } else if (res == ENQ) {
                log.debug(inetAddress + "нам хотят что-то прислать");
                sb.setLength(0);
                transport4results.sendMessage("<ACK>");
            } else if (res == STX) {
                String msg = transport4results.readMessage();
                sb.append(msg);
                transport4results.sendMessage("<ACK>");
            } else if (res == EOT) {
                log.debug(inetAddress + "мы зафиксировали конец передачи");
                System.out.println(sb.toString());
                return sb.toString();
            } else {
                log.error(inetAddress + "ошибка протокола");
            }
        } while (res != ERROR_TIMEOUT);
        return null;
    }
}
