package ru.idc.citm;

import ru.idc.citm.drivers.CitmDriver;
import ru.idc.citm.base.Configuration;
import ru.idc.citm.base.DBManager;
import ru.idc.citm.base.IDriver;
import ru.idc.citm.drivers.Medonic;

public class Manager {

	public static void main(String[] args) throws InterruptedException {
		Configuration config;
		while (true) {
			config = new Configuration();
			try {
				DBManager dbManager = new DBManager();
				try {
					dbManager.init(config);

					IDriver driver = getDriverByName(config.getParamValue("driver"));
					if (driver != null) {
						driver.init(dbManager, config);
						driver.loop();
					} else {
						System.out.println("Недопустимое имя драйвера: " + config.getParamValue("driver"));
					}
				} finally {
					dbManager.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Thread.sleep(10000);
			}
		}
	}

	private static IDriver getDriverByName(String name) {
		switch (name) {
			case "CITM": return new CitmDriver();
			case "MEDONIC":	return new Medonic();
			default: return null;
		}
	}
}
