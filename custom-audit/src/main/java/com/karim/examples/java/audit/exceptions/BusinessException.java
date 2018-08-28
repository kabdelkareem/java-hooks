package com.karim.examples.java.audit.exceptions;

/**
 * All application exceptions (Business/Java Exceptions) wrapper in service layer to {@link BusinessException}
 */
public class BusinessException extends Exception{
	private static final long serialVersionUID = 1L;

	public BusinessException(String message){
		super(message);
	}
}