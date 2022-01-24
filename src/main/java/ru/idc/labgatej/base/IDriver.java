package ru.idc.labgatej.base;

import java.io.IOException;
import java.sql.SQLException;

public interface IDriver{
	void loop() throws IOException, InterruptedException, SQLException;
	//	void init(DBManager dbManager, Configuration config);
	void init(DriverContext driverContext);
	void close();

	/**
	 * Останавливаем работу драйвера. В большинстве случаев передается в
	 * DriverContext параметр running. Он останавливает работу цикла в методе
	 * loop. В некоторых случаях требуется прервать системные ожидания.
	 * Например, остановить работу сокета.
	 */
	void stop();
}
