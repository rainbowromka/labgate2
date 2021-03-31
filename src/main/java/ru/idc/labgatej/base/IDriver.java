package ru.idc.labgatej.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.IOException;
import java.sql.SQLException;

public interface IDriver {
	void loop() throws IOException, InterruptedException, SQLException;
//	void init(DBManager dbManager, Configuration config);
	void init(ComboPooledDataSource connectionPool, Configuration config);
	void close();
}
