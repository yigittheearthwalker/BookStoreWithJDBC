package com.bookstore.domain;

public class Writer extends Element{
	
	public static int counter = 0;
	
	private int id;
	private String writer_name;
	private int number_of_books;
	
	public Writer() {
		this.id = ++counter;
		BookStore.bookStoreElementsToInsert.add(0, this);
	}
	public Writer(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getWriter_name() {
		return writer_name;
	}
	public void setWriter_name(String writer_name) {
		this.writer_name = writer_name;
	}
	public int getNumber_of_books() {
		return number_of_books;
	}
	public void setNumber_of_books(int number_of_books) {
		this.number_of_books = number_of_books;
	}
	@Override
	public boolean ensureDataExistance() {
		if (this.writer_name == null || this.writer_name.equals("")) {
			return false;
		}else {
			return true;
		}
	}
}
