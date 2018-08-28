package com.karim.examples.java.audit.exceptions;

import com.karim.examples.java.audit.dal.DataAccess;

/**
 * It throws from {@link DataAccess} during DML operation if something went wrong during CRUD operations
 * It used to make all layer don't depend on what EF used.
 */
public class DatabaseException extends Exception{
	private static final long serialVersionUID = 1L;

	public DatabaseException(String message){
		super(message);
	}
}
