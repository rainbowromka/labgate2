package ru.idc.citm;

import ru.idc.citm.model.Order;
import ru.idc.citm.model.PacketInfo;

import java.io.*;
import java.util.List;

import static ru.idc.citm.Codes.*;
import static ru.idc.citm.Consts.ERROR_TIMEOUT;

public class Driver {
	private Transport transport;
	private DBManager dbManager;
	private Protocol protocol;

	public Driver(Protocol protocol, DBManager dbManager, Transport transport) {
		this.protocol = protocol;
		this.dbManager = dbManager;
		this.transport  = transport;
	}

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
					Utils.logMessage("нас готовы слушать");
					transport.sendMessage(msg);
					res = transport.readInt();
					if (res == Codes.ACK) {
						Utils.logMessage("Наше сообщение приняли");
						// нужно помечать задание как обработанное в БД
						dbManager.markOrderAsProcessed(taskId);
					} else if (res == Codes.NAK) {
						Utils.logMessage("Наше сообщение не понравилось почему-то");
						hasErrors = true;
					} else {
						Utils.logMessage("ошибка протокола");
						hasErrors = true;
					}
					transport.sendMessage("<EOT>");
				} else if (res == Codes.NAK) {
					Utils.logMessage("нас не готовы слушать, ждём 10 секунд");
					// надо подождать не меньше 10 секунд
					hasErrors = true;
				} else {
					Utils.logMessage("ошибка протокола");
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
				Utils.logMessage("ждём данных");
			} else if (res == ENQ) {
				Utils.logMessage("нам хотят что-то прислать");
				sb.setLength(0);
				transport.sendMessage("<ACK>");
			} else if (res == STX) {
				String msg = transport.readMessage();
				sb.append(msg);
				transport.sendMessage("<ACK>");
			} else if (res == EOT) {
				Utils.logMessage("мы зафиксировали конец передачи");
				System.out.println(sb.toString());
				return sb.toString();
			} else {
				Utils.logMessage("ошибка протокола");
			}
		} while (res != ERROR_TIMEOUT);
		return null;
	}

	public void loop() throws IOException, InterruptedException {
		if (!transport.isReady()) return;

		String msg, answer;
		int res;
		while (true) {
			sendTasks();
			msg = receiveResults();
			if (msg != null) {
				PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
				dbManager.saveResults(packetInfo);
			}
			Thread.sleep(500);
		}
		// Utils.logMessage("отключаемся");
	}
}
