package com.bookstore.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
	public static Connection connect() {
		
		Connection connection = null;
		try {
			 connection = DriverManager.getConnection(DBVariables.DB_URL, DBVariables.DB_USERNAME, DBVariables.DB_PASSWORD);
			 System.out.println("DB Connection Established...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	}
}
