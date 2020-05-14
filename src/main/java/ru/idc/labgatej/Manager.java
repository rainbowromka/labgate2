package ru.idc.labgatej;

import org.apache.log4j.Level;
import org.apache.log4j.spi.RootLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.drivers.CitmDriver;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.drivers.Medonic;

import static org.apache.log4j.Level.INFO;

public class Manager {
	private static Logger logger = LoggerFactory.getLogger(Manager.class);

	public static void main(String[] args) throws InterruptedException {
		System.out.println("ТЕстовый текст !!!!!!!!!!!!!!!!!!!!!!!!");
		logger.trace("Запуск приложения");
		Configuration config;
		while (true) {
			logger.trace("Чтение конфигурации");
			config = new Configuration();
			try {
				RootLogger.getRootLogger().setLevel(Level.toLevel(config.getParamValue("log.level"), INFO));

				DBManager dbManager = new DBManager();
				try {
					logger.trace("Инициализация подключения к БД...");
					dbManager.init(config);

					IDriver driver = getDriverByName(config.getParamValue("driver"));
					if (driver != null) {
						driver.init(dbManager, config);
						driver.loop();
					} else {
						logger.error("Недопустимое имя драйвера: " + config.getParamValue("driver"));
					}
				} finally {
					dbManager.close();
				}
			} catch (Exception e) {
				logger.error("", e);
				logger.debug("Ждём 10 секунд перед перезапуском...");
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