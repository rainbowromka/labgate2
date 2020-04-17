package ru.idc.citm;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		while (true) {
			try {
				DBManager dbManager = new DBManager();
				try {
					dbManager.init();
					Protocol protocol = new ProtocolASTM();
					Transport transport = new SocketClientTransport("192.168.17.192", 1100); //citm-serv.dc-local
					transport.init();
					Driver driver = new Driver(protocol, dbManager, transport);
					driver.loop();
					//break;
				} finally {
					dbManager.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Thread.sleep(10000);
			}
		}
	}
}
