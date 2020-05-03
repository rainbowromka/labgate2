package ru.idc.labgatej.base;

import java.io.IOException;

public interface IDriver {
	void loop() throws IOException, InterruptedException;
	void init(DBManager dbManager, Configuration config);
}
