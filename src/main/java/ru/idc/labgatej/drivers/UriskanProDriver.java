package ru.idc.labgatej.drivers;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.StopBits;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.base.protocols.Protocol;
import ru.idc.labgatej.base.protocols.ProtocolUriskanPro;
import ru.idc.labgatej.base.Rs232ClientTransport;
import ru.idc.labgatej.base.SocketClientTransport;
import ru.idc.labgatej.base.Transport;
import ru.idc.labgatej.model.PacketInfo;

import java.io.IOException;
import java.sql.SQLException;

import static ru.idc.labgatej.base.Codes.*;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;
import static ru.idc.labgatej.base.Consts.WRONG_DATA;

@Slf4j
public class UriskanProDriver
implements IDriver
{
	private Transport transport;
	private DBManager dbManager;
	private Protocol protocol;
	private String deviceCode;

	private String receiveResults() throws IOException {
		StringBuilder sb = new StringBuilder();
		int res;
		do {
			try {
				res = transport.readInt(true);
			} catch (NumberFormatException e) {
				res = WRONG_DATA;
			}

			if (res == ERROR_TIMEOUT) {
				log.trace("ждём данных");
			} else if (res == STX) {
				log.debug("начинается пакет данных");
				sb.setLength(0);
				for (int i = 0; i < 17; i++) {
					String msg = transport.readMessage();
					sb.append(msg);
					//System.out.println(msg);
				}
				return sb.toString();
			} else {
				log.debug("нам что-то не то прислали");
			}
		} while (res != ERROR_TIMEOUT && res != ETX);
		return null;
	}

	@Override
	public void loop() throws IOException, InterruptedException, SQLException {
		if (!transport.isReady()) return;

		String msg = null;
		while (true) {
			msg = receiveResults();

			if (msg != null && !msg.isEmpty()) {
				log.debug("Получили сообщение: " + msg);
				PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
				packetInfo.setDeviceCode(deviceCode);

				dbManager.saveResults(packetInfo, false);
			}
			Thread.sleep(200);
		}
	}

	@Override
	public void init(
		ComboPooledDataSource connectionPool,
		Configuration config)
	{
		this.dbManager = new DBManager();
		dbManager.init(connectionPool);
		protocol = new ProtocolUriskanPro();
		deviceCode = config.getParamValue("code");

		switch (config.getParamValue("device.connection.type").toUpperCase().trim()) {
			case "COM":
				transport = new Rs232ClientTransport(config.getParamValue("device.connection.port"));
				transport.init(2000, 9600, DataBits.DATABITS_8, Parity.NONE, StopBits.STOPBITS_1, FlowControl.NONE);
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
