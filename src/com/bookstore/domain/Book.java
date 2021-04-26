package com.bookstore.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.bookstore.exceptions.MoreThanOneException;
import com.bookstore.utils.DBConnector;

public class Book extends Element{
	
	public static int counter = 0;
	
	private int id;
	private String book_name;
	private int release_year;
	private int publisher_id;
	private int writer_id;
	
	public Publisher publisher = null;
	public Writer writer = null;
	
	public Book(String book_name, String writerName, String publisherName) {
		this.id = ++counter;
		this.book_name = book_name;
		
		//Bellow Code is to figure out if the publisher and the writer existing. If not, creates them.
		//It pushes the Writer and Publisher to the first index and the book to the last index of the persistance array
		//To somehow ensure not to get foreign key violations
		
		int pid = checkPublisher(publisherName);
		int wid = checkWriter(writerName);
		
		if (pid == 0) {
			this.publisher = new Publisher();
			this.publisher.setPublisher_name(publisherName);
			this.publisher_id = this.publisher.getId();
		}else {
			this.publisher_id = pid;
		}
		if(wid == 0) {
			this.writer = new Writer();
			this.writer.setWriter_name(writerName);
			this.writer_id = this.writer.getId();
		}else {
			this.writer_id = wid;
		}
		BookStore.bookStoreElementsToInsert.add(BookStore.bookStoreElementsToInsert.size(),this);
	}
	public Book(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBook_name() {
		return book_name;
	}
	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}
	public int getPublisher_id() {
		return publisher_id;
	}
	public void setPublisher_id(int publisher_id) {
		this.publisher_id = publisher_id;
	}
	public int getWriter_id() {
		return writer_id;
	}
	public void setWriter_id(int writer_id) {
		this.writer_id = writer_id;
	}

	public int getRelease_year() {
		return release_year;
	}

	public void setRelease_year(int release_year) {
		this.release_year = release_year;
	}

	@Override
	public boolean ensureDataExistance() {
		if (this.book_name == null || this.book_name.equals("") 
				|| this.release_year == 0 || this.publisher_id == 0 || this.writer_id == 0) {
			return false;
		}else {
			return true;
		}
	}
	int checkPublisher(String name) {
		int id = 0;
		for (Element element : BookStore.bookStoreElementsToInsert) {
			if (element.getClass().getSimpleName().equalsIgnoreCase("Publisher")) {
				Publisher publisher = (Publisher) element;
				if (publisher.getPublisher_name().equalsIgnoreCase(name)) {
					return publisher.getId();
				}
			}
		}
		try (Connection connection = DBConnector.connect()){
			PreparedStatement ps = connection.prepareStatement("SELECT id FROM publisher where publisher_name = ?");
			ps.setString(1, name);
			
			ResultSet result = ps.executeQuery();
			
			if (result.next()) {
				id = result.getInt("id");
				  if (result.next()) {
				    throw new MoreThanOneException("There are more than one publisher with the name " + name);
				  }
				} else {
				  return id;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	int checkWriter(String name) {
		int id = 0;
		for (Element element : BookStore.bookStoreElementsToInsert) {
			if (element.getClass().getSimpleName().equalsIgnoreCase("Writer")) {
				Writer writer = (Writer) element;
				if (writer.getWriter_name().equalsIgnoreCase(name)) {
					return writer.getId();
				}
			}
		}
		try (Connection connection = DBConnector.connect()){
			PreparedStatement ps = connection.prepareStatement("SELECT id FROM writer where writer_name = ?");
			ps.setString(1, name);
			
			ResultSet result = ps.executeQuery();
			
			if (result.next()) {
				id = result.getInt("id");
				  if (result.next()) {
				    throw new MoreThanOneException("There are more than one writer with the name " + name);
				  }
				} else {
				  return id;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
}
