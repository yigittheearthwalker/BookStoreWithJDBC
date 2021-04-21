package com.bookstore.interfaces;

public interface Persistable<T> {
	void persist() throws Exception;
}
