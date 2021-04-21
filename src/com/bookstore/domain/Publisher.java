package com.bookstore.domain;

public class Publisher extends Element{
	
	public static int counter = 0;

	private int id;
	private String publisher_name;
	private int number_of_books;
	
	public Publisher() {
		this.id = ++counter;
		BookStore.bookStoreElementsToInsert.add(0, this);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPublisher_name() {
		return publisher_name;
	}
	public void setPublisher_name(String publisher_name) {
		this.publisher_name = publisher_name;
	}
	public int getNumber_of_books() {
		return number_of_books;
	}
	public void setNumber_of_books(int number_of_books) {
		this.number_of_books = number_of_books;
	}

	@Override
	public boolean ensureDataExistance() {
		if (this.publisher_name == null || this.publisher_name.equals("")) {
			return false;
		}else {
			return true;
		}
	}
	
	
}
