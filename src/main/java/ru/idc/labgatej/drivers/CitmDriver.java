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

	private Transport transport4tasks;
	private Transport transport4results;
	private DBManager dbManager4results;
	private DBManager dbManager4tasks;
	private Protocol protocol;
	private boolean createAliquot;

	private void sendTasks() throws IOException {
		String msg;
		List<Order> orders;
		boolean hasErrors = false;
		long taskId;
		int cnt = 0;
		int res;
		do {
			orders = dbManager4tasks.getOrders();
			msg = protocol.makeOrder(orders);
			if (!msg.isEmpty()) {
				taskId = orders.get(0).getTaskId();
				transport4tasks.sendMessage("<ENQ>");
				res = transport4tasks.readInt();
				if (res == Codes.ACK) {
					logger.debug("нас готовы слушать");
					transport4tasks.sendMessage(msg);
					res = transport4tasks.readInt();
					if (res == Codes.ACK) {
						logger.debug("Наше сообщение приняли");
						// нужно помечать задание как обработанное в БД
						dbManager4tasks.markOrderAsProcessed(taskId);
						if (createAliquot) {
							dbManager4tasks.registerAliquots(orders);
						}
					} else if (res == Codes.NAK) {
						logger.debug("Наше сообщение не понравилось почему-то");
						hasErrors = true;
						dbManager4tasks.markOrderAsFailed(taskId, "Наше сообщение не понравилось почему-то");
					}	else {
						logger.error("ошибка протокола");
						hasErrors = true;
						dbManager4tasks.markOrderAsFailed(taskId, "ошибка протокола");
					}
					transport4tasks.sendMessage("<EOT>");
				} else if (res == Codes.NAK) {
					logger.debug("нас не готовы слушать, ждём 10 секунд");
					// надо подождать не меньше 10 секунд
					hasErrors = true;
				} else {
					if (res == Codes.ENQ) {
						logger.error("нас перебили");
					} else {
						logger.error("ошибка протокола");
					}
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
			res = transport4results.readInt(true);
			if (res == ERROR_TIMEOUT) {
				logger.trace("ждём данных");
			} else if (res == ENQ) {
				logger.debug("нам хотят что-то прислать");
				sb.setLength(0);
				transport4results.sendMessage("<ACK>");
			} else if (res == STX) {
				String msg = transport4results.readMessage();
				sb.append(msg);
				transport4results.sendMessage("<ACK>");
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
		if (!transport4tasks.isReady()) return;
		if (!transport4results.isReady()) return;

		Runnable task = () -> {
			try {
				while (! Thread.currentThread().isInterrupted()) {
					sendTasks();
				}
			} catch (IOException e) {
				logger.error("Ошибка в потоке отправки заданий", e);
			}
		};

		Thread thread = new Thread(task);
		thread.start();

		String msg = null;
		while (true) {
			if (!thread.isAlive()) {
				throw new IOException();
			}

			msg = receiveResults();

			if (msg != null && !msg.isEmpty()) {
				PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
				dbManager4results.saveResults(packetInfo, true);
			}
			Thread.sleep(500);
		}
	}

	@Override
	public void init(DBManager dbManager, Configuration config) {
		this.dbManager4results = dbManager;
		this.dbManager4tasks = new DBManager();
		logger.trace("Инициализация второго подключения к БД...");
		dbManager4tasks.init(config);

		protocol = new ProtocolASTM();
		transport4tasks = new SocketClientTransport(config.getParamValue("citm.server"), Integer.parseInt(config.getParamValue("citm.port.task")));
		transport4tasks.init(2000);
		transport4results = new SocketClientTransport(config.getParamValue("citm.server"), Integer.parseInt(config.getParamValue("citm.port.result")));
		transport4results.init(10000);

		createAliquot = !"off".equalsIgnoreCase(config.getParamValue("citm.aliquot"));
	}

	@Override
	public void close() {
		transport4tasks.close();
		transport4results.close();
		dbManager4tasks.close();
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
