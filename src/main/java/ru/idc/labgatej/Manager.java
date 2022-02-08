package ru.idc.labgatej;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.spi.RootLogger;
import ru.idc.labgatej.base.AppPooledDataSource;
import ru.idc.labgatej.base.DriverContext;
import ru.idc.labgatej.base.IConfiguration;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.base.ISendClientMessages;
import ru.idc.labgatej.drivers.DriverFactory;

import java.beans.PropertyVetoException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.log4j.Level.INFO;

/**
 * Менеджер драйвера. Создает экземпляр драйвера, конфигурирует его, запускает и
 * управляет потоком. Менеджер может работать как самостоятельное Java
 * приложение, так и под управлением Spring Boot.
 */
@Slf4j
public class Manager {

	/**
	 * Пул доступа к базе данных.
	 */
	private ComboPooledDataSource cpds;

	/**
	 * Конфигурация экземпляра драйвера.
	 */
	private IConfiguration config;

	/**
	 * Признак того что приложение должно работать. При переведении этого
	 * свойства в false, драйвер остановится после выполнения текущих операций.
	 */
	private AtomicBoolean running;

	/**
	 * Экземпляр драйвера, который необходимо запустить.
	 */
	private IDriver driver = null;

	/**
	 * Передача сообщений клиентскому приложению.
	 */
	private ISendClientMessages sendClientMessages;

	/**
	 * Точка входа в Java-приложение. Драйвер будет работать как обычное
	 * приложение.
	 *
	 * @param args
	 *        командная строка.
	 * @throws InterruptedException
	 *         генерируемое исключение.
	 * @throws PropertyVetoException
	 *         генерируемое исключение.
	 */
	public static void main(
		String[] args)
	throws InterruptedException, PropertyVetoException
	{
		Configuration config = new Configuration();
		AppPooledDataSource appPooledDataSource
			= new AppPooledDataSource(config);

		log.trace("Инициализация пула подключения к БД...");
		new Manager(config, appPooledDataSource.getCpds(), null).runManager();

	}

	/**
	 * Создает объект менеджера экземпляра драйвера.
	 * @param config
	 *        конфигурация экземпляра драйвера.
	 * @param cpds
	 *        пул доступа к базе данных.
	 * @param sendClientMessages
	 *        отправка сообщений клиентскому приложению.
	 */
	public Manager(
		IConfiguration config,
		ComboPooledDataSource cpds,
		ISendClientMessages sendClientMessages
	)
	{
		this.config = config;
		this.cpds = cpds;
		running = new AtomicBoolean(true);
		this.sendClientMessages = sendClientMessages;
	}

	/**
	 * Получает объект драйвера, конфигурирует его и запускает его. Этот метод
	 * должен запускаться в отдельном потоке под управлением Spring Boot.
	 *
	 * @throws InterruptedException
	 *         генерируемое исключение.
	 */
	public void runManager()
	throws InterruptedException
	{
		log.trace("Запуск приложения");

		DriverContext driverContext = new DriverContext(cpds, config, running, sendClientMessages);

		while (running.get()) {
			log.trace("Чтение конфигурации");
			try {
				RootLogger.getRootLogger().setLevel(Level.toLevel(config.getParamValue("log.level"), INFO));

				DBManager dbManager = new DBManager();
				try {
					//TODO: Дополнительно настроить пул соединений.
					dbManager.init(cpds);

					driver = DriverFactory.getDriverByName(config.getDriverName());
					if (driver != null) {
						driver.init(driverContext);
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
		driverContext.getSendClientMessages().sendDriverIsStopped(config);
	}

	/**
	 * Останавливает драйвер из другого потока. Используется под управлением
	 * Spring Boot. В случае с обычным Java приложением. Посылается команда,
	 * прерывания работы приложения.
	 */
	public void stop()
	{
		running.set(false);

		if (driver != null)
		{
			driver.stop();
		}
	}
}
