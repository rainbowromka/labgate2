package ru.idc.labgatej.base;

import java.io.IOException;
import java.sql.SQLException;

public interface IDriver {
	void loop() throws IOException, InterruptedException, SQLException;
	void init(DBManager dbManager, Configuration config);
	void close();
}
