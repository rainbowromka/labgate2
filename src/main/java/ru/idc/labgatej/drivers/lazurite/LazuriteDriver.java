package ru.idc.labgatej.drivers.lazurite;

import com.google.common.primitives.Bytes;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.StopBits;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.Rs232ClientTransport;
import ru.idc.labgatej.base.SocketClientTransport;
import ru.idc.labgatej.base.TaskDualDriver;
import ru.idc.labgatej.base.Transport;
import ru.idc.labgatej.base.protocols.Protocol;
import ru.idc.labgatej.base.protocols.ProtocolCITM_ASTM;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.PacketInfo;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.idc.labgatej.base.Codes.*;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

@Slf4j
public class LazuriteDriver extends TaskDualDriver<ProtocolCITM_ASTM> {

	private Protocol protocol;
	private String deviceCode;

	@Override
	public String receiveResults() throws IOException {
		StringBuilder sb = new StringBuilder();
		int dataSize;
		int res;
		int crc;
		int msgId;
		int frameNumber;
		int frameLength;
		byte[] data;
		List<Byte> allData = new ArrayList<>();

		do {
			dataSize = transport4results.readInt(true);
			if (dataSize == ERROR_TIMEOUT) {
				res = ERROR_TIMEOUT;
				log.trace("ждём данных");
			} else {
				log.trace("Получили размер пакета: " + dataSize);
				log.trace("Читаем код типа пакета...");
				res = transport4results.readInt(false);
				if (res == SOM) {
					log.debug("начинается сообщение");
					crc = transport4results.readInt(false);
					msgId = transport4results.readInt(false);
					frameNumber = transport4results.readInt(false);
					frameLength = transport4results.readInt(false);
					data = transport4results.readData(1);
					log.trace("Получили пакет <SOM>");

					sb.setLength(0);
					log.debug("Отправляем готовность к приему фреймов...");
					transport4results.sendInt(12);
					transport4results.sendMessage("<ACK>");
					transport4results.sendInt(msgId);
					transport4results.sendMessage("<SOM>");
					log.trace("Отправили <ACK>");

					// читаем фреймы
					do {
						log.trace("Читаем фрейм...");
						// читаем фрейм
						dataSize = transport4results.readInt(true);
						if (dataSize == ERROR_TIMEOUT) {
							res = ERROR_TIMEOUT;
							log.error("Нет фреймов!");
							break;
						} else {
							log.trace("Получили размер пакета: " + dataSize);
							log.trace("Читаем код типа пакета...");

							res = transport4results.readInt(false); //FRM или DONETRANS
							if (res == FRM) {
								log.trace("Получили пакет <FRM>");
								crc = transport4results.readInt(false);
								msgId = transport4results.readInt(false);
								frameNumber = transport4results.readInt(false);
								frameLength = transport4results.readInt(false);
								data = transport4results.readData(frameLength);

								allData.addAll(Bytes.asList(data));
							} else if (res != DONETRANS) {
								log.debug("нам что-то не то прислали");
							}
						}
					} while (res != DONETRANS);
					if (res == DONETRANS) {
						log.trace("Получили пакет <DONETRANS>");
						msgId = transport4results.readInt(false);

						log.trace("Отправляем <DONERECV>");
						transport4results.sendInt(8);
						transport4results.sendMessage("<DONERECV>");
						transport4results.sendInt(msgId);
					}

					return Arrays.toString(allData.toArray());
				} else {
					log.debug("нам что-то не то прислали");
					break;
				}
			}
		} while (res != ERROR_TIMEOUT);
		return null;
	}

	@Override
	protected List<Order> getOrders() throws SQLException {
		return null;
	}

	@Override
	protected String protocolMakeOrder(List<Order> orders) {
		return null;
	}

	@Override
	public void loop() throws IOException, InterruptedException, SQLException {
		if (!transport4results.isReady()) return;

		String msg = null;
		while (true) {
			msg = receiveResults();

			if (msg != null && !msg.isEmpty()) {
				log.debug("Получили сообщение: " + msg);
				List<PacketInfo> packetInfos = protocol.parseMessage(makeSendable(msg));
				packetInfos.forEach(p -> p.setDeviceCode(deviceCode));

				dbManager4results.saveResults(packetInfos, false);
			}
			Thread.sleep(200);
		}
	}

	public void init(ComboPooledDataSource connectionPool, Configuration config, Socket socket) {
		super.init(connectionPool, config);

		transport4results = new SocketClientTransport(null, 0);
		transport4results.init(socket, 10000);

		protocol = new ProtocolLazuriteASTM();
		deviceCode = config.getParamValue("code");



//		switch (config.getParamValue("device.connection.type").toUpperCase().trim()) {
//			case "COM":
//				transport = new Rs232ClientTransport(config.getParamValue("device.connection.port"));
//				transport.init(2000, 9600, DataBits.DATABITS_8, Parity.NONE, StopBits.STOPBITS_1, FlowControl.NONE);
//				break;
//			case "TCP_SERVER":
//				break;
//
//			default:
//				transport = new SocketClientTransport(config.getParamValue("device.connection.host"),
//					Integer.parseInt(config.getParamValue("device.connection.port")));
//				transport.init(10000);
//		}
	}

}
