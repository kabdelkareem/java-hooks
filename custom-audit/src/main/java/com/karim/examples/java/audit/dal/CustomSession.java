package com.karim.examples.java.audit.dal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Session;

import com.karim.examples.java.audit.dal.orm.BaseEntity;
import com.karim.examples.java.audit.exceptions.DatabaseException;

/**
 * Encapsulate the hibernate session using adapter design pattern
 * to remove the decoupling between our code and hibernate so it can be
 * easily replaced by others JPA vendors
 *
 */
public class CustomSession {
    private Session session;
	private List<BaseEntity> insertList = new ArrayList<BaseEntity>();
    
    // Package visibility to be used only by Data Access Class.
    CustomSession(Session session){
        this.session = session;
    }

    // Package visibility to be used only by this package.
    Session getSession() {
        return this.session;
    }
    
    //-----------------------------------------------------------------------------
    /**
     * Encapsulate the session begin transaction.
     */
    public void beginTransaction() {
    	try{
    		session.beginTransaction();
    	} catch (RuntimeException e){
    		e.printStackTrace();
    		throw e;
    	}
    }

    
    /**
     * Encapsulate the session save.
     * 
     * @param objToBeSaved
     */
    void save(Object objToBeSaved) {
    	try{
    		session.save(objToBeSaved);
    		if(objToBeSaved instanceof BaseEntity)
    			insertList.add((BaseEntity) objToBeSaved);
    	} catch (RuntimeException e){
    		e.printStackTrace();
    		throw e;
    	}
    }
    
    /**
     * Encapsulate the session update.
     * 
     * @param objToBeUpdated
     */
    void update(Object objToBeUpdated) {
    	try{
    		session.update(objToBeUpdated);
    	} catch (RuntimeException e){
    		e.printStackTrace();
    		throw e;
    	}
    }

    /**
     * Encapsulate the session delete.
     * 
     * @param objToBeDeleted
     */
    void delete(Object objToBeDeleted) {
    	try{
    		session.delete(objToBeDeleted);
    	} catch (RuntimeException e){
    		e.printStackTrace();
    		throw e;
    	}
    }

    /**
     * Encapsulate the session commit transaction.
     */
    public void commitTransaction() {
    	try{
    		session.getTransaction().commit();
    		insertList.clear();
    	} catch (javax.persistence.PersistenceException e){
    		e.printStackTrace();
   			throw e;
    	} catch (RuntimeException e){
    		e.printStackTrace();
    		throw e;
    	}
    }
    

    /**
     * Encapsulate the session rollback transaction.
     */
    public void rollbackTransaction() {		
        try{
        	session.getTransaction().rollback();
        	for (BaseEntity entity : insertList) {
    			entity.setId(null);
    		}
        	insertList.clear();
    	} catch (RuntimeException e){
    		e.printStackTrace();
    		throw e;
    	}
    }

    /**
     * 
     * @param hqlQuery HQL query string
     * @param parameters {@link Map} of parameters
     * 
     * @return the result of executing the query
     * 
     * @throws DatabaseException f something went wrong
     */
    @SuppressWarnings("unchecked")
	<T> List<T> getResultList(String hqlQuery, Map<String, Object> parameters)  throws DatabaseException {
		try {
			Query q = session.createQuery(hqlQuery);

			if (parameters != null) {
				for (String paramName : parameters.keySet()) {
					Object value = parameters.get(paramName);
					q.setParameter(paramName, value);
				}
			}	

			return (List<T>) q.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
	}

    /**
     * Encapsulate the session close transaction.
     */
    public void close(){		
    	try{
    		session.close();		
    	}catch(RuntimeException e){
    		e.printStackTrace();
    		throw e;
    	}	
    }
    
    /**
     * Check if session isActive
     */
    boolean isActive(){		
    	try{
    		return session.getTransaction().isActive();		
    	}catch(RuntimeException e){
    		e.printStackTrace();
    		return false;
    	}	
    }
}