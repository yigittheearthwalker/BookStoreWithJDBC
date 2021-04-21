package com.bookstore.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.bookstore.exceptions.MissingRequiredFieldsException;
import com.bookstore.exceptions.NoSuchElementException;
import com.bookstore.interfaces.Persistable;
import com.bookstore.utils.DBConnector;

public class BookStore implements Persistable<Element>{
	
	public static ArrayList<Element> bookStoreElementsToInsert = new ArrayList<>();

	
	@Override
	public void persist() throws Exception {		
		for (Element element : bookStoreElementsToInsert) {
			if (!element.ensureDataExistance()) {
				throw new MissingRequiredFieldsException("There are missing fields that needs to be filled for " + element.getClass().getSimpleName());
			}			
			System.out.println(element.getClass().getSimpleName() + " is about to be persisted via prepared statement...");
			try (Connection connection = DBConnector.connect()){
				String query = queryBuilder(element);
				PreparedStatement pStatement = connection.prepareStatement(query);
				Field[] fields = element.getClass().getDeclaredFields();
				int indexCounter = 1;
				for (int i = 1; i < fields.length; i++) {
					if (Modifier.isPrivate(fields[i].getModifiers())) {
						fields[i].setAccessible(true);
						if (String.class.isAssignableFrom(fields[i].getType())) {
							pStatement.setString(indexCounter, (String) fields[i].get(element));
						}else {
							pStatement.setInt(indexCounter, (int) fields[i].get(element));	
						}
						fields[i].setAccessible(false);
						indexCounter++;
					}
				}
				if (element.getClass().getSimpleName().equalsIgnoreCase("Book")) {
					Book book = (Book) element;
					int publisher_id = book.getPublisher_id();
					int writer_id = book.getWriter_id();
					pStatement.setInt(indexCounter++, publisher_id);
					pStatement.setInt(indexCounter++, publisher_id);
					pStatement.setInt(indexCounter++, writer_id);
					pStatement.setInt(indexCounter++, writer_id);
				}
				pStatement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		bookStoreElementsToInsert = new ArrayList<>();
	}
	
	
	String queryBuilder(Object object) {
		String query = "INSERT INTO"
				+ " " + object.getClass().getSimpleName().toLowerCase() + " (";
		String fieldNames = "";
		String values = "";
		Field[] fields = object.getClass().getDeclaredFields();
		
		for (int i = 0; i < fields.length; i++) {
			if (Modifier.isPrivate(fields[i].getModifiers())) {
				fieldNames += fields[i].getName() + ", ";
				values += "?, ";
			}
		}
		query += fieldNames;
		query = query.substring(0, (query.length() - 2)) + ") VALUES (";
		query += values;
		query = query.substring(0, (query.length() - 2)) +  "); ";
		if (object.getClass().getSimpleName().equalsIgnoreCase("Book")) {
			query += "\n UPDATE publisher p SET number_of_books = (select count(*) from book where publisher_id = ?) where id = ?;"
					+ " UPDATE writer p SET number_of_books = (select count(*) from book where writer_id = ?) where id = ?;";
		}
		return query;
	}	
	
}
