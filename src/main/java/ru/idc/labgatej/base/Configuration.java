package ru.idc.labgatej.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private static Logger logger = LoggerFactory.getLogger(Configuration.class);
	private Properties params;

	public Configuration() {
		try (InputStream input = new FileInputStream("config.properties")) {
			params = new Properties();
			params.load(input);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public String getParamValue(String param) {
		return params.getProperty(param);
	}
}
