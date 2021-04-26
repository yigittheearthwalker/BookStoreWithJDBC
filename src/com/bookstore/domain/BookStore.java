package com.bookstore.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.bookstore.exceptions.MissingRequiredFieldsException;
import com.bookstore.interfaces.Persistable;
import com.bookstore.utils.DBOperationsHandler;

public class BookStore implements Persistable<Element>{
	
	public static ArrayList<Element> bookStoreElementsToInsert = new ArrayList<>();
	public static ArrayList<Element> bookStoreFetchedElements = new ArrayList<>();

	@Override
	public void persist() {
		for (Element element : bookStoreFetchedElements) {
			DBOperationsHandler.update(element);
		}
		for (Element element : bookStoreElementsToInsert) {
			try {
				DBOperationsHandler.insert(element);
			} catch (MissingRequiredFieldsException e) {
				e.printStackTrace();
			}
		}
		bookStoreElementsToInsert = new ArrayList<>();
		bookStoreFetchedElements = new ArrayList<>();
	}
	
	public Element get(Class<? extends Element> elementClass, int id) {
		Element element = null;
			try {
				element = DBOperationsHandler.get((Element) elementClass.getDeclaredConstructor(int.class).newInstance(id), id);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		bookStoreFetchedElements.add(element);
		return element;
	}
	
	public void clean() {
		bookStoreElementsToInsert = new ArrayList<>();
		bookStoreFetchedElements = new ArrayList<>();
	}
}
