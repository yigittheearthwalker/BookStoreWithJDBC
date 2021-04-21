package com.bookstore.main;

import com.bookstore.domain.Book;
import com.bookstore.domain.BookStore;
import com.bookstore.domain.Publisher;
import com.bookstore.domain.Writer;
import com.bookstore.utils.DBConnector;
import com.bookstore.utils.DBInitializer;

public class Driver {
	public static void main(String[] args) {
		DBInitializer.init(DBConnector.connect());
		BookStore bookStore = new BookStore();
		
		
		//Some Example Object Creations are below
		//Unlike the others Book class requires you to specify Writer and Publiher name in the constructor
		//In order to make sure if such record exists or if not to create it so not to catch a foreign key violation
		Publisher publisher = new Publisher();
		publisher.setPublisher_name("Ayrıntı Yayınları");
		Writer writer = new Writer();
		writer.setWriter_name("Orhan Pamuk");
		
		Book book = new Book("1984", "George Orwell", "Can Yayınları");
		book.setRelease_year(1949);
		Book book2 = new Book("Hayvan Çiftliği", "George Orwell", "Can Yayınları");
		book2.setRelease_year(1945);
		
		Writer writer2 = new Writer();
		writer2.setWriter_name("Turgut Uyar");
		
		Book book3 = new Book("Büyük Saat", "Turgut Uyar", "Can Yayınları");
		book3.setRelease_year(1984);
		
		Publisher publisher2 = new Publisher();
		publisher2.setPublisher_name("Yapı Kredi Yayınları");
		
		Publisher publisher3 = new Publisher();
		publisher3.setPublisher_name("Metis Yayınları");
		
		Publisher publisher4 = new Publisher();
		publisher4.setPublisher_name("Ithaki Yayınları");
		
		try {
			bookStore.persist();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}