package ru.idc.labgatej.drivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.base.Protocol;
import ru.idc.labgatej.base.ProtocolASTM;
import ru.idc.labgatej.base.SocketClientTransport;
import ru.idc.labgatej.base.Transport;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.PacketInfo;

import java.io.*;
import java.util.List;

import static ru.idc.labgatej.base.Codes.*;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

public class CitmDriver implements IDriver {
	private static Logger logger = LoggerFactory.getLogger(CitmDriver.class);
	private Transport transport;
	private DBManager dbManager;
	private Protocol protocol;

	private void sendTasks() throws IOException {
		String msg;
		List<Order> orders;
		boolean hasErrors = false;
		long taskId;
		int cnt = 0;
		int res;
		do {
			orders = dbManager.getOrders();
			msg = protocol.makeOrder(orders);
			if (!msg.isEmpty()) {
				taskId = orders.get(0).getTaskId();
				transport.sendMessage("<ENQ>");
				res = transport.readInt();
				if (res == Codes.ACK) {
					logger.debug("нас готовы слушать");
					transport.sendMessage(msg);
					res = transport.readInt();
					if (res == Codes.ACK) {
						logger.debug("Наше сообщение приняли");
						// нужно помечать задание как обработанное в БД
						dbManager.markOrderAsProcessed(taskId);
						dbManager.registerAliquots(orders);
					} else if (res == Codes.NAK) {
						logger.debug("Наше сообщение не понравилось почему-то");
						hasErrors = true;
						dbManager.markOrderAsFailed(taskId, "Наше сообщение не понравилось почему-то");
					} else {
						logger.error("ошибка протокола");
						hasErrors = true;
						dbManager.markOrderAsFailed(taskId, "ошибка протокола");
					}
					transport.sendMessage("<EOT>");
				} else if (res == Codes.NAK) {
					logger.debug("нас не готовы слушать, ждём 10 секунд");
					// надо подождать не меньше 10 секунд
					hasErrors = true;
				} else {
					logger.error("ошибка протокола");
					hasErrors = true;
				}
				cnt++;
			}
		} while (!hasErrors && !msg.isEmpty() && cnt < 5);
	}

	private String receiveResults() throws IOException {
		StringBuilder sb = new StringBuilder();
		int res;
		do {
			res = transport.readInt(true);
			if (res == ERROR_TIMEOUT) {
				logger.debug("ждём данных");
			} else if (res == ENQ) {
				logger.debug("нам хотят что-то прислать");
				sb.setLength(0);
				transport.sendMessage("<ACK>");
			} else if (res == STX) {
				String msg = transport.readMessage();
				sb.append(msg);
				transport.sendMessage("<ACK>");
			} else if (res == EOT) {
				logger.debug("мы зафиксировали конец передачи");
				System.out.println(sb.toString());
				return sb.toString();
			} else {
				logger.error("ошибка протокола");
			}
		} while (res != ERROR_TIMEOUT);
		return null;
	}

	@Override
	public void loop() throws IOException, InterruptedException {
		if (!transport.isReady()) return;

		String msg;
		while (true) {
			sendTasks();
			msg = receiveResults();
			//msg = makeResults();
			if (msg != null) {
				PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
				dbManager.saveResults(packetInfo, true);
			}
			Thread.sleep(500);
		}
	}

	@Override
	public void init(DBManager dbManager, Configuration config) {
		this.dbManager = dbManager;
		protocol = new ProtocolASTM();
		transport = new SocketClientTransport(config.getParamValue("citm.server"), Integer.parseInt(config.getParamValue("citm.port")));
		transport.init();
	}

	// для отладки
	private String makeResults() {

		return "1H|\\^&|6347||^^^|||||||P||20200517130941<CR><ETX>B6<CR><LF>" +
				   "2P|1||?||^||||||||||||||||||20200517122737||<CR><ETX>43<CR><LF>" +
			     "3O|1|20005480||ALL|?|20200517122737|||||X||||1||||||||||F<CR><ETX>20<CR><LF>" +
			     "4R|1|^^^108^1^^^COBAS_CCEE^^^^^^|3.890|mmol/l||N||F||&S&SYSTEM^System||20200517080941|COBAS_CCEE<CR><ETX>65<CR><LF>" +
			     "5L|1|N<CR><ETX>08<CR><LF>";

//		return "1H|\\^&|5653||^^^|||||||P||20200516145049<CR><ETX>B9<CR><LF>" +
//					 "2P|1||?||^||||||||||||||||||20200516131727||<CR><ETX>41<CR><LF>" +
//					 "3O|1|20005317||ALL|?|20200516131727|||||X||||1||||||||||F<CR><ETX>1D<CR><LF>" +
//					 "4R|1|^^^44^1^^^COBAS_CCEE^^^^^^|49.000|umol/l||N||C||&S&SYSTEM^System||20200516092429|COBAS_CCEE<CR><ETX>65<CR><LF>" +
//					 "5L|1|N<CR><ETX>08<CR><LF>";
	}
}
