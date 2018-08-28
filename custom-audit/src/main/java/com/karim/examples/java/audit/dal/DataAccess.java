package com.karim.examples.java.audit.dal;

import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.karim.examples.java.audit.dal.orm.AuditEntity;
import com.karim.examples.java.audit.dal.orm.BaseEntity;
import com.karim.examples.java.audit.enums.LogOperationTypeEnum;
import com.karim.examples.java.audit.exceptions.DatabaseException;
import com.karim.examples.java.audit.services.audit.AuditService;

/**
 * 
 * @author kabdelkareem
 *
 *	represents the data access layer that user must use to make CRUD
 *	operations on the database.
 */

public final class DataAccess {
    private static SessionFactory sessionFactory;
    
    private DataAccess(){}
    
    public static void init()  {
    	if(sessionFactory == null){
    		StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
    		Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();
    		sessionFactory = metaData.getSessionFactoryBuilder().build();
        }
    }
	
    public static CustomSession getSession() {
        return new CustomSession(sessionFactory.openSession());
    }
    
    public static void addEntity(BaseEntity bean, CustomSession csession) throws DatabaseException {
        if(csession == null || !csession.isActive()) {
        	throw new DatabaseException("Database session not active, Please contact system administrator.");
        }
        
        try {   
        	csession.save(bean);
 
        	// Audit business
            try{
            	AuditService.insertAuditLogAndAuditLogDetail(bean, LogOperationTypeEnum.INSERT, csession);
    		}catch(Throwable logEx){
    			// Do nothing as I believe failure on audit service must not reflect main transaction in regular systems
    		}
            
        }
        catch (RuntimeException ex) {
            throw new DatabaseException("General Error, Please contact system administrator.");
        }
    }
    
    public static void updateEntity(BaseEntity bean, CustomSession csession) throws DatabaseException {
        if(csession == null || !csession.isActive()) {
        	throw new DatabaseException("Database session not active, Please contact system administrator.");
        }
        
        try {   
        	csession.update(bean);
 
        	// Audit business
            try{
            	AuditService.insertAuditLogAndAuditLogDetail(bean, LogOperationTypeEnum.UPDATE, csession);
    		}catch(Throwable logEx){
    			// Do nothing as I believe failure on audit service must not reflect main transaction in regular systems
    		}
            
        }
        catch (RuntimeException ex) {
            throw new DatabaseException("General Error, Please contact system administrator.");
        }
    }
    
    public static void deleteEntity(BaseEntity bean, CustomSession csession) throws DatabaseException {
        if(csession == null || !csession.isActive()) {
        	throw new DatabaseException("Database session not active, Please contact system administrator.");
        }
        
        try {   
        	csession.delete(bean);
 
        	// Audit business
            try{
            	AuditService.insertAuditLogAndAuditLogDetail(bean, LogOperationTypeEnum.DELETE, csession);
    		}catch(Throwable logEx){
    			// Do nothing as I believe failure on audit service must not reflect main transaction in regular systems
    		}
            
        }
        catch (RuntimeException ex) {
            throw new DatabaseException("General Error, Please contact system administrator.");
        }
    }

    public static void addEntityLog(AuditEntity bean, CustomSession csession) throws DatabaseException {
    	if(csession == null || !csession.isActive()) {
        	throw new DatabaseException("Database session not active, Please contact system administrator.");
        }
        
        try {   
        	csession.save(bean);
        }
        catch (RuntimeException ex) {
            throw new DatabaseException("General Error, Please contact system administrator.");
        }
    }
    

	public static List<?> executeHqlQuery(String hqlQuery, Map<String, Object> parameters, CustomSession... sessions) throws DatabaseException{
    	boolean isSessionExist = false;
    	if(sessions != null && sessions.length != 0)
    		isSessionExist = true;
    	
    	CustomSession csession = isSessionExist? sessions[0] : DataAccess.getSession();
    	try {
    		return csession.getResultList(hqlQuery, parameters);
    	} finally {
    		 if(isSessionExist) csession.close();
		}
    }
   
	
    public static void close(){
		if(sessionFactory != null){
			sessionFactory.close();
			sessionFactory = null;
		}
	}
}