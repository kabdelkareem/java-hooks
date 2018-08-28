package com.karim.examples.java.audit.services.test;

import com.karim.examples.java.audit.dal.CustomSession;
import com.karim.examples.java.audit.dal.DataAccess;
import com.karim.examples.java.audit.dal.orm.test.TestEntity;
import com.karim.examples.java.audit.exceptions.BusinessException;
import com.karim.examples.java.audit.services.BaseService;

public abstract class TestService extends BaseService {
	
	public static void insertTestEntity(TestEntity testObject) throws BusinessException {
        CustomSession csession = DataAccess.getSession();

        try {
	        csession.beginTransaction();
	        DataAccess.addEntity(testObject, csession);
	        csession.commitTransaction();
        } catch(Throwable e) {
        	csession.rollbackTransaction();
        	throw new BusinessException(e.getMessage());
        } finally {
			csession.close();
		}
        
	}
	
	public static void updateTestEntity(TestEntity testObject) throws BusinessException {
        CustomSession csession = DataAccess.getSession();

        try {
	        csession.beginTransaction();
	        DataAccess.updateEntity(testObject, csession);
	        csession.commitTransaction();
        } catch(Throwable e) {
        	csession.rollbackTransaction();
        	throw new BusinessException(e.getMessage());
        } finally {
			csession.close();
		}
        
	}
	
	public static void deleteTestEntity(TestEntity testObject) throws BusinessException {
        CustomSession csession = DataAccess.getSession();

        try {
	        csession.beginTransaction();
	        DataAccess.deleteEntity(testObject, csession);
	        csession.commitTransaction();
        } catch(Throwable e) {
        	csession.rollbackTransaction();
        	throw new BusinessException(e.getMessage());
        } finally {
			csession.close();
		}
        
	}
	
}
