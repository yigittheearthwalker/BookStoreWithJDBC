package com.bookstore.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.util.PSQLException;

import com.bookstore.domain.Book;
import com.bookstore.domain.Publisher;
import com.bookstore.domain.Writer;

public class DBInitializer {
	public static void init(Connection connection) {
		try {
			Statement statement = connection.createStatement();
			String query = "CREATE TABLE IF NOT EXISTS book("
					+ "id INT,"
					+ "book_name VARCHAR(60) NOT NULL,"
					+ "release_year INT,"
					+ "publisher_id INT NOT NULL,"
					+ "writer_id INT NOT NULL,"
					+ "PRIMARY KEY (id)"
					+ ");"
					+ "CREATE TABLE IF NOT EXISTS publisher("
					+ "id INT,"
					+ "publisher_name VARCHAR(30) NOT NULL,"
					+ "number_of_books INT,"
					+ "PRIMARY KEY(id)"
					+ ");"
					+ "CREATE TABLE IF NOT EXISTS writer("
					+ "id INT,"
					+ "writer_name VARCHAR(50) NOT NULL,"
					+ "number_of_books INT,"
					+ "PRIMARY KEY(id)"
					+ ");"
					+ "ALTER TABLE book ADD FOREIGN KEY (publisher_id) REFERENCES publisher(id);"
					+ "ALTER TABLE book ADD UNIQUE (book_name);"
					+ "ALTER TABLE publisher ADD UNIQUE (publisher_name);"
					+ "ALTER TABLE writer ADD UNIQUE (writer_name);"
					+ "ALTER TABLE book ADD FOREIGN KEY (writer_id) REFERENCES writer(id);";
			 statement.execute(query); 
		}catch (PSQLException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		countElements();
	}
	
	//Counts Elements in the DB to manage ID assignments in the static fields of each object
	static void countElements() {
		try (Connection connection = DBConnector.connect()) {
			Statement statement = connection.createStatement();
			String query = "SELECT "
				 		+ " count(id) as publisherCount,"
				 		+ " (SELECT count(*) from book) as bookCount,"
				 		+ " (SELECT count(*) from writer) as writerCount"
				 		+ " FROM publisher;";
			 ResultSet result = statement.executeQuery(query);
			
			 while(result.next()) {
				 if (result.isLast()) {
					 Publisher.counter = result.getInt("publisherCount");
					 	Book.counter = result.getInt("bookCount");
					 	Writer.counter = result.getInt("writerCount");
				 } 	
				}
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
