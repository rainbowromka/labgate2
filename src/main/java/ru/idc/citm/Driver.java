package ru.idc.citm;

import java.io.*;

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
		String msg, answer;
		int res;
		msg = protocol.makeOrder(dbManager.getBarcodes());
		if (!msg.isEmpty()) {
			transport.sendMessage("<ENQ>");
			res = transport.readInt();
			if (res == Codes.ACK) {
				Utils.logMessage("нас готовы слушать");
				transport.sendMessage(msg);
				res = transport.readInt();
				transport.sendMessage("<EOT>");
			} else {
				Utils.logMessage("нас не готовы слушать");
			}
		}
	}

	public void loop() throws IOException, InterruptedException {
		if (!transport.isReady()) return;

		String msg, answer;
		int res;
		while (true) {
			sendTasks();
			Utils.logMessage("отключаемся");
			break;
			// Thread.sleep(100);
		}
	}
}
