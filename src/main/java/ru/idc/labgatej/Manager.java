package ru.idc.labgatej;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.spi.RootLogger;
import ru.idc.labgatej.drivers.CitmDriver;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.drivers.DNATechnologyDriver.DNATechnplogyDriver;
import ru.idc.labgatej.drivers.CsvImporter;
import ru.idc.labgatej.drivers.KDLPrime.KdlPrime;
import ru.idc.labgatej.drivers.KdlMax.KdlMaxDriverAstmDual;
import ru.idc.labgatej.drivers.Medonic;
import ru.idc.labgatej.drivers.MultiskanFC;
import ru.idc.labgatej.drivers.RealBest;
import ru.idc.labgatej.drivers.UriskanProDriver;

import static org.apache.log4j.Level.INFO;

@Slf4j
public class Manager {

	private static ComboPooledDataSource cpds = new ComboPooledDataSource();

	public static void main(String[] args) throws InterruptedException {
		runManager(args);
	}

	public static void runManager(String[] arg)
	throws InterruptedException
	{
		log.trace("Запуск приложения");
		Configuration config;
		IDriver driver = null;
		while (true) {
			log.trace("Чтение конфигурации");
			config = new Configuration();
			try {
				RootLogger.getRootLogger().setLevel(Level.toLevel(config.getParamValue("log.level"), INFO));

				DBManager dbManager = new DBManager();
				try {
					log.trace("Инициализация пула подключения к БД...");
					cpds.setDriverClass("org.postgresql.Driver");
					cpds.setJdbcUrl(config.getParamValue("db.url"));
					cpds.setUser(config.getParamValue("db.user"));
					cpds.setPassword(config.getParamValue("db.password"));
					//TODO: Дополнительно настроить пул соединений.
					dbManager.init(cpds);

					driver = getDriverByName(config.getParamValue("driver"));
					if (driver != null) {
						driver.init(cpds, config);
						driver.loop();
					} else {
						log.error("Недопустимое имя драйвера: " + config.getParamValue("driver"));
					}
				} finally {
					dbManager.close();
				}
			} catch (Exception e) {
				if (driver != null) {
					try {
						driver.close();
					} catch (Exception e2) {
						log.error("Ошибка при завершении работы драйвера", e2);
					}
				}
				log.error("", e);
				log.debug("Ждём 60 секунд перед перезапуском...");
				Thread.sleep(60000);
			}
		}
	}

	private static IDriver getDriverByName(String name) {
		switch (name.toUpperCase().trim()) {
			case "CITM": return new CitmDriver();
			case "MEDONIC":	return new Medonic();
			case "MULTISKANFC":	return new MultiskanFC();
			case "URISKAN": return new UriskanProDriver();
			case "REALBEST": return new RealBest();
			case "DNATEACH": return new DNATechnplogyDriver();
			case "CSVIMPORTER": return new CsvImporter();
			case "KDLMAX": return new KdlMaxDriverAstmDual();
			case "KDLPrime": return new KdlPrime();
			default: return null;
		}
	}
}
