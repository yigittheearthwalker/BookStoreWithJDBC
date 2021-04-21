package com.bookstore.domain;

public abstract class Element{
	private int id;

	public abstract boolean ensureDataExistance ();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
