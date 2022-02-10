package ru.idc.labgatej.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс конфигурации из файла config.properties. Используется старой простой
 * версией драйвера.
 */
public class Configuration
implements IConfiguration
{
	private static Logger logger = LoggerFactory.getLogger(Configuration.class);

	/**
	 * Параметры.
	 */
	private Properties params;

	/**
	 * Создает класс конфигурации.
	 */
	public Configuration() {
		try (InputStream input = new FileInputStream("config.properties")) {
			params = new Properties();
			params.load(input);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	@Override
	public String getParamValue(String param) {
		return params.getProperty(param);
	}

	@Override
	public String getDriverName()
	{
		return params.getProperty("driver");
	}

	@Override
	public Long getDriverId() {
		return null;
	}

	@Override
	public void setStatus(
		DriverStatus status)
	{
	}

	@Override
	public String getDriverInstanceName()
	{
		// Т.к. данная имплементация конфигурации драйвера используется в
		// Embedded приложениях то тут можно возвращать любое значение, главное
		// что бы это не нарушало принцип логгирования. В иных случаях нужно
		// вовзвращать экземпляр драйвера.
		return "embedded";
	}
}
