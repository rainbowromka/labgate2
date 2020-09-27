package ru.idc.labgatej.drivers;

import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.StopBits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.base.Protocol;
import ru.idc.labgatej.base.ProtocolASTM;
import ru.idc.labgatej.base.Rs232ClientTransport;
import ru.idc.labgatej.base.SocketClientTransport;
import ru.idc.labgatej.base.Transport;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.PacketInfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static ru.idc.labgatej.base.Codes.*;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

public class UriskanProDriver implements IDriver {
	private static Logger logger = LoggerFactory.getLogger(UriskanProDriver.class);

	private Transport transport;
	private DBManager dbManager;
	private Protocol protocol;

	private String receiveResults() throws IOException {
		StringBuilder sb = new StringBuilder();
		int res;
		do {
			res = transport.readInt(true);
			if (res == ERROR_TIMEOUT) {
				logger.trace("ждём данных");
			} else {
				logger.debug("нам хотят что-то прислать");
				sb.setLength(0);
				String msg = transport.readMessage();
				sb.append(msg);
				System.out.println(sb.toString());
				return sb.toString();
			}
		} while (res != ERROR_TIMEOUT);
		return null;
	}

	@Override
	public void loop() throws IOException, InterruptedException, SQLException {
		if (!transport.isReady()) return;

		String msg = null;
		while (true) {
			msg = receiveResults();

			if (msg != null && !msg.isEmpty()) {
				logger.debug("Получили сообщение: " + msg);
//				PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
//				dbManager.saveResults(packetInfo, true);
			}
			Thread.sleep(200);
		}
	}

	@Override
	public void init(DBManager dbManager, Configuration config) {
		this.dbManager = dbManager;
		protocol = new ProtocolASTM();

		switch (config.getParamValue("device.connection.type").toUpperCase().trim()) {
			case "COM":
				transport = new Rs232ClientTransport(config.getParamValue("device.connection.port"));
				transport.init(2000,9600, DataBits.DATABITS_8, Parity.NONE, StopBits.STOPBITS_1, FlowControl.NONE);
				break;

			default:
				transport = new SocketClientTransport(config.getParamValue("device.connection.host"),
					Integer.parseInt(config.getParamValue("device.connection.port")));
				transport.init(10000);
		}




	}

	@Override
	public void close() {
		transport.close();
	}

}
