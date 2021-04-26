package com.bookstore.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bookstore.domain.Book;
import com.bookstore.domain.Element;
import com.bookstore.domain.Publisher;
import com.bookstore.domain.Writer;
import com.bookstore.exceptions.MissingRequiredFieldsException;

public class DBOperationsHandler {
	 public static void insert(Element element) throws MissingRequiredFieldsException {
		 if (!element.ensureDataExistance()) {
				throw new MissingRequiredFieldsException("There are missing fields that needs to be filled for " + element.getClass().getSimpleName());
			}			
			System.out.println(element.getClass().getSimpleName() + " is about to be persisted via prepared statement...");
			try (Connection connection = DBConnector.connect()){
				String query = insertQueryBuilder(element);
				PreparedStatement pStatement = connection.prepareStatement(query);
				Field[] fields = element.getClass().getDeclaredFields();
				int indexCounter = 1;
				for (int i = 0; i < fields.length; i++) {
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
			} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} 
	 }
	 public static void update (Element element) {
		 try (Connection connection = DBConnector.connect()){
			 PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + element.getClass().getSimpleName().toLowerCase() + " WHERE id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			 Publisher p = null;
			 Writer w = null;
			 Book b = null;
			 if (element.getClass().getSimpleName().equals("Publisher")) {
				 p = (Publisher) element;
				 ps.setInt(1, p.getId());
			 }else if(element.getClass().getSimpleName().equals("Writer")) {
				 w = (Writer) element;
				 ps.setInt(1, w.getId());
			 }else if(element.getClass().getSimpleName().equals("Book")) {
				 b = (Book) element;
				 ps.setInt(1, b.getId());
			 }
			 ResultSet result = ps.executeQuery();
				while (result.next()) {
					boolean hasChanges = false;
					if (element.getClass().getSimpleName().equals("Publisher")) {
						if (!p.getPublisher_name().equalsIgnoreCase(result.getString("publisher_name"))) {
							result.updateString("publisher_name", p.getPublisher_name());
							hasChanges = true;
						}
						if (p.getNumber_of_books() != (result.getInt("number_of_books"))) {
							result.updateInt("number_of_books", p.getNumber_of_books());
							hasChanges = true;
						}						
					}else if(element.getClass().getSimpleName().equals("Writer")) {
						if (!w.getWriter_name().equalsIgnoreCase(result.getString("writer_name"))) {
							result.updateString("writer_name", w.getWriter_name());
							hasChanges = true;
						}
						if (w.getNumber_of_books() != (result.getInt("number_of_books"))) {
							result.updateInt("number_of_books", w.getNumber_of_books());
							hasChanges = true;
						}	
					}else if(element.getClass().getSimpleName().equals("Book")){
						if (!b.getBook_name().equalsIgnoreCase(result.getString("book_name"))) {
							result.updateString("book_name", b.getBook_name());
							hasChanges = true;
						}
						if (b.getRelease_year() != (result.getInt("release_year"))) {
							result.updateInt("release_year", b.getRelease_year());
							hasChanges = true;
						}
						if (b.getPublisher_id() != (result.getInt("publisher_id"))) {
							result.updateInt("publisher_id", b.getPublisher_id());
							hasChanges = true;
						}
						if (b.getWriter_id() != (result.getInt("writer_id"))) {
							result.updateInt("writer_id", b.getWriter_id());
							hasChanges = true;
						}
					}
					if (hasChanges) {
						result.updateRow();	
					}
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
	 public static Element get(Element element, int id) {
		 try (Connection connection = DBConnector.connect()){
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + element.getClass().getSimpleName().toLowerCase() + " WHERE id = ?");
			ps.setInt(1, id);
			ResultSet result = ps.executeQuery();
			while (result.next()) {
				if (element.getClass().getSimpleName().equals("Publisher")) {
					Publisher publisher = (Publisher) element;
					publisher.setPublisher_name(result.getString("publisher_name"));
					publisher.setNumber_of_books(result.getInt("number_of_books"));
				}else if(element.getClass().getSimpleName().equals("Writer")) {
					Writer writer = (Writer) element;
					writer.setWriter_name(result.getString("writer_name"));
					writer.setNumber_of_books(result.getInt("number_of_books"));
				}else {
					Book book = (Book) element;
					book.setBook_name(result.getString("book_name"));
					book.setRelease_year(result.getInt("release_year"));
					book.setPublisher_id(result.getInt("publisher_id"));
					book.setWriter_id(result.getInt("writer_id"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return element;
	 }
	 static String insertQueryBuilder(Object object) {
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
