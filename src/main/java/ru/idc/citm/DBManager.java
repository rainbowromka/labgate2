package ru.idc.citm;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class DBManager {
	//  Database credentials
	private static final String DB_URL = "jdbc:postgresql://192.168.17.248:5432/dcdb";
	private static final String USER = "gis";
	private static final String PASS = "1";

	public Connection dbConnection = null;

	public void init() {
		System.out.println("Testing connection to PostgreSQL JDBC");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
			e.printStackTrace();
			return;
		}

		System.out.println("PostgreSQL JDBC Driver successfully connected");
		dbConnection = null;

		try {
			dbConnection = DriverManager
				.getConnection(DB_URL, USER, PASS);

		} catch (SQLException e) {
			System.out.println("Connection Failed");
			e.printStackTrace();
			return;
		}

		if (dbConnection != null) {
			System.out.println("You successfully connected to database now");
		} else {
			System.out.println("Failed to make connection to database");
		}
	}

	public void close() {
		if (dbConnection != null) {
			try {
				System.out.println("Closing connection to PostgreSQL");
				dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getBarcodes() {
		return Collections.singletonList("123456");
	}
}
