package com.bookstore.exceptions;

public class MissingRequiredFieldsException extends Exception{
	public MissingRequiredFieldsException(String message) {
		super(message);
	}
}
