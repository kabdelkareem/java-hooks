package com.karim.examples.java.audit;

public class MockVaraibles {
	// Current Module Code
	public static final String CURRENT_MODULE = "SYS_NAME";
	
	/*
	 * If web  application, you can fill this with current session's user during every request ,as each request has separate thread, and clear it before response send back 
	 * 	by using 
	 *  	J2EE: Filters
	 * 		JSF:  PhaseListener
	 * 
	 * If desktop application you can set it during login as it'll be one thread application 
	 */
	public static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<String>() {
		@Override
		protected String initialValue() {
			return "sys";
		}
	};
}
