package com.karim.examples.java.audit.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.karim.examples.java.audit.dal.CustomSession;

public abstract class BaseService {
	protected static boolean isSessionOpened(CustomSession[] sessions) {
		if (sessions != null && sessions.length > 0)
			return true;
		return false;
	}
	

	/**
	 * Makes a deep copy of any Java object that is passed.
	 */
	protected static Object deepCopy(Object object) {
	   try {
	     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	     ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
	     outputStrm.writeObject(object);
	     ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
	     ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
	     return objInputStream.readObject();
	   }
	   catch (Exception e) {
	     e.printStackTrace();
	     return null;
	   }
	 }

}
