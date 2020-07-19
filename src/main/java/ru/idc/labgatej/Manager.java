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
import ru.idc.labgatej.drivers.RealBest;

import static org.apache.log4j.Level.INFO;

public class Manager {
	private static Logger logger = LoggerFactory.getLogger(Manager.class);

	public static void main(String[] args) throws InterruptedException {
		logger.trace("Запуск приложения");
		Configuration config;
		IDriver driver = null;
		while (true) {
			logger.trace("Чтение конфигурации");
			config = new Configuration();
			try {
				RootLogger.getRootLogger().setLevel(Level.toLevel(config.getParamValue("log.level"), INFO));

				DBManager dbManager = new DBManager();
				try {
					logger.trace("Инициализация подключения к БД...");
					dbManager.init(config);

					driver = getDriverByName(config.getParamValue("driver"));
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
				if (driver != null) {
					try {
						driver.close();
					} catch (Exception e2) {
						logger.error("Ошибка при завершении работы драйвера", e2);
					}
				}
				logger.error("", e);
				logger.debug("Ждём 60 секунд перед перезапуском...");
				Thread.sleep(60000);
			}
		}
	}

	private static IDriver getDriverByName(String name) {
		switch (name) {
			case "CITM": return new CitmDriver();
			case "MEDONIC":	return new Medonic();
			case "REALBEST": return new RealBest();
			default: return null;
		}
	}
}
