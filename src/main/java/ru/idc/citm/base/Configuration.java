package ru.idc.citm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private Properties params;

	public Configuration() {
		try (InputStream input = new FileInputStream("config.properties")) {
			params = new Properties();
			params.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getParamValue(String param) {
		return params.getProperty(param);
	}
}
