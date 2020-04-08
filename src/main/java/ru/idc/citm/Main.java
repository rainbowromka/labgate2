package ru.idc.citm;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		DBManager dbManager = new DBManager();
		try {
			dbManager.init();
			Protocol protocol = new ProtocolASTM();
			Transport transport = new SocketClientTransport("citm-serv.dc-local", 1100);
			transport.init();
			Driver driver = new Driver(protocol, dbManager, transport);
			while (true) {
				try {
					driver.loop();
					break;
				} catch (Exception e) {
					e.printStackTrace();
					Thread.sleep(10000);
				}
			}
		} finally {
			dbManager.close();
		}
	}
}
