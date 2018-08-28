package com.karim.examples.java.audit.services.audit;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.karim.examples.java.audit.MockVaraibles;
import com.karim.examples.java.audit.dal.Auditable;
import com.karim.examples.java.audit.dal.CustomSession;
import com.karim.examples.java.audit.dal.DataAccess;
import com.karim.examples.java.audit.dal.orm.BaseEntity;
import com.karim.examples.java.audit.dal.orm.audit.AuditLogData;
import com.karim.examples.java.audit.dal.orm.audit.AuditLogDetailData;
import com.karim.examples.java.audit.enums.LogOperationTypeEnum;
import com.karim.examples.java.audit.exceptions.BusinessException;
import com.karim.examples.java.audit.exceptions.DatabaseException;
import com.karim.examples.java.audit.services.BaseService;

/**
 * Insert audience transactions into the database
 * 
 * @author Karim Abd ElKareem
 * @since 15/7/2015
 */
public class AuditService extends BaseService {
	/**
	 * Insert audience transactions into the database extracted from the changed entity if it's {@link Auditable#enabled()} is true
	 * 
	 * @param entity represents the changed entity
	 * @param operation represents the change operation type ({@link LogOperationTypeEnum#INSERT}, {@link LogOperationTypeEnum#UPDATE}, 
	 *          {@link LogOperationTypeEnum#DELETE})
	 * @param session represents the session
	 * @throws BusinessException if something went wrong.
	 */
	public static void insertAuditLogAndAuditLogDetail(BaseEntity entity, LogOperationTypeEnum operation, CustomSession ... session) throws BusinessException {
		//Disable log for all entities that not have annotation {@link Auditable} or not enabled
		if(!entity.getClass().isAnnotationPresent(Auditable.class) || !entity.getClass().getAnnotation(Auditable.class).enabled()) 
			return;
		
		//Check session status
		boolean isOpenedSession = isSessionOpened(session); 
		//Get the session if exist or create a new session
		CustomSession csSession = isOpenedSession ? session[0] : DataAccess.getSession();
		try {
			//If a new session, start the transaction
			if(!isOpenedSession) csSession.beginTransaction();
			
			
			Class<?> cls = entity.getClass();
			Table table = cls.getAnnotation(Table.class);
			//Contains the id of the table as 'propertyId1=value1,propertyId2=value2'
		 	String entityId = "";
			//Contains the id of the table as 'columnId1=value1,columnId2=value2'
			String tableId = "";
			Method[] methods = cls.getDeclaredMethods();
			for (Method method : methods) { // Loop over all methods in the entity
				if(method.isAnnotationPresent(Id.class)){ // Check if the method has a annotation Id
					String methodName = method.getName();
					//Get the property name from it's javaBean method
					String prop = Introspector.decapitalize(methodName.substring(methodName.startsWith("is") ? 2 : 3));
					//Get the field 
					Field field = cls.getDeclaredField(prop);
					//Convert the access modifier for the field to public to enable direct access
					field.setAccessible(true);	
					//Add to entity ids the name/value pair of the property as JPA syntax
					entityId += field.getName() + "=" + field.get(entity) + ",";
					//Add to entity ids the name/value pair as database  syntax
					tableId += (method.getAnnotation(Column.class) == null? prop.toUpperCase() : method.getAnnotation(Column.class).name()) + "=" + field.get(entity) + ",";
					//Convert the access modifier for the field to private to disable direct access
					field.setAccessible(false);
				}
			}
			//Remove last comma
			entityId = entityId.substring(0, entityId.lastIndexOf(","));
			tableId = tableId.substring(0, tableId.lastIndexOf(","));
			
			BaseEntity oldRow = null;
			if(operation != LogOperationTypeEnum.INSERT)
			{
				//Build the HQL query
				StringBuffer selectQuery = new StringBuffer("FROM " + entity.getClass().getSimpleName() + " WHERE " + entityId.replace(",", " AND "));
				//Execute the HQL query and get the row.
				try {
					oldRow = (BaseEntity) DataAccess.executeHqlQuery(selectQuery.toString(), null).get(0);
				} catch (Exception e) {
					if(operation == LogOperationTypeEnum.UPDATE) {
						operation = LogOperationTypeEnum.INSERT;
					} else {
						e.printStackTrace();
						throw new BusinessException("error_logException");
					}
				} 
			}
			
			//Build audit log based on entity
			AuditLogData auditLog = buildLogMessage(entity, operation, table, tableId, oldRow);
			//Insert the new audit log
			DataAccess.addEntityLog(auditLog, csSession);

			//Build detailed audit log based on entity
			List<AuditLogDetailData> auditLogDetailList = buildLogMessageDetails(entity, operation, oldRow);
			for(AuditLogDetailData  auditLogDetail : auditLogDetailList){ // Loop over changes log
				//Set the audit log reference
				auditLogDetail.setAuditLogId(auditLog.getId());
				//Insert the new audit log detail
				DataAccess.addEntityLog(auditLogDetail, csSession);
			}
			//If a new session, commit the transaction
			if(!isOpenedSession) csSession.commitTransaction();
		} catch (DatabaseException e) {
			e.printStackTrace();
			if(!isOpenedSession) csSession.rollbackTransaction();
			throw new BusinessException("error_dbError");
		} catch (BusinessException e){
			if(!isOpenedSession) csSession.rollbackTransaction();
			throw e;
		} catch (Throwable e){
			e.printStackTrace();
			if(!isOpenedSession) csSession.rollbackTransaction();
			throw new BusinessException("error_general");
		} finally {
			//If a new session, close the session
			if(!isOpenedSession)csSession.close();
		}
	}
	
	//******************************************* Audit Log  *********************************************************
	/**
	 * Build the {@link AuditLogData} object for the changed entity
	 * 
	 * @param entity represents the changed entity
	 * @param operation represents the change operation type ({@link LogOperationTypeEnum#INSERT}, {@link LogOperationTypeEnum#UPDATE}, {@link LogOperationTypeEnum#DELETE})
	 * @param table represent {@link Table} information associated with entity
	 * @param tableId represent the current entity id
	 * @param oldRow represent the old entity to be used in case of update or delete
	 * @throws BusinessException if something went wrong.
	 */
	private static AuditLogData buildLogMessage(BaseEntity entity, LogOperationTypeEnum operation, Table table, String tableId, BaseEntity oldRow)  throws BusinessException{
		// Get another copy from the entity instead of original
		entity = (BaseEntity) deepCopy(entity);
		oldRow = (BaseEntity) deepCopy(oldRow);
		
		AuditLogData auditLogData = new AuditLogData();
		try {
			//Set logged table name
			auditLogData.setLogTable(table.name());
			
			//Set logged operation type
			auditLogData.setOperationType(operation);
			
			//Set row id as database syntax
			auditLogData.setLogRowId(tableId);
			
			// Loop over methods and remove attribute that have property @Auditable(enabled=false)
			Class<?> cls = entity.getClass();
			Method[] methods = cls.getDeclaredMethods();
			
			for (Method method : methods) { // Loop over all methods in the entity
				if(method.isAnnotationPresent(Auditable.class) 
						&& !method.getAnnotation(Auditable.class).enabled() 
						&& !method.getReturnType().isPrimitive()) { // Check if the method has a annotation @Auditable and type is object
					String methodName = method.getName();
					//Get the property name from it's javaBean method
					String prop = Introspector.decapitalize(methodName.substring(methodName.startsWith("is") ? 2 : 3));
					//Get the field 
					Field field = cls.getDeclaredField(prop);
					//Convert the access modifier for the field to public to enable direct access
					field.setAccessible(true);	
					
					//Empty the value on current entity & old entity
					field.set(entity, null);
					if(oldRow != null) field.set(oldRow, null);
					
					//Convert the access modifier for the field to private to disable direct access
					field.setAccessible(false);
				}
			}
			
			//Set the row before update with a version from the row in the database if the operation not insert
			if (operation != LogOperationTypeEnum.INSERT) {
				// Set the row before update with the json representation of the row object.
				String logRowBefore = new Gson().toJson(oldRow);
				auditLogData.setLogRowDataBefore(logRowBefore);
			}

			// Set the row after update with the new entity object if the operation not delete
			if (operation != LogOperationTypeEnum.DELETE) {
				// Set the row after update with the json representation of the entity.
				String logRowAfter = new Gson().toJson(entity);
				auditLogData.setLogRowDataAfter(logRowAfter);
			}
			
			//Set the module code
			auditLogData.setModuleCode(MockVaraibles.CURRENT_MODULE);
			
			//Set the user id from the session or sys if internal transaction
			auditLogData.setUserId(MockVaraibles.CURRENT_USER.get());
			
			
			//Set the operation date to the server current date
			auditLogData.setOperationDate(new Date());
			
		} catch (Exception e) { //There are a problem during the send of the log message.
			e.printStackTrace();
			throw new BusinessException("error_logException");
		}
		return auditLogData;
	}

	//**************************************** Audit Log Detail *******************************************************
	/**
	 * Build a list from {@link AuditLogDetailData} object for all changes in the entity
	 * 
	 * @param entity represents the changed entity
	 * @param operation represents the change operation type ({@link LogOperationTypeEnum#INSERT}, {@link LogOperationTypeEnum#UPDATE}, {@link LogOperationTypeEnum#DELETE})
	 * @param oldRow represent the old entity to be used in case of update or delete
	 * @throws BusinessException if something went wrong.
	 */
	private static List<AuditLogDetailData> buildLogMessageDetails(BaseEntity entity, LogOperationTypeEnum operation, BaseEntity oldRow) 
	             throws BusinessException{
		List<AuditLogDetailData> auditLogDetailDataList = new ArrayList<AuditLogDetailData>();
		try {
			//Get the effected table name.
			Class<?> cls = entity.getClass();
			Method[] methods = cls.getDeclaredMethods();
			
			
			for (Method method : methods) { // Loop over all methods in the entity
				// Skip transient attributes
				if(method.isAnnotationPresent(Transient.class)) 
					continue;
				String methodName = method.getName();
				
				// Skip not getter methods because annotation put on getters.
				if(!methodName.startsWith("is") &&  !methodName.startsWith("get"))
					continue;

				//Get the property name from it's javaBean method
				String prop = Introspector.decapitalize(methodName.substring(methodName.startsWith("is") ? 2 : 3));
				//Get the field 
				Field field = cls.getDeclaredField(prop);
				//Convert the access modifier for the field to public to enable direct access
				field.setAccessible(true);
				
				//Initialize a new object from type {@link AuditLogDetailData}
				AuditLogDetailData auditLogDetailData = new AuditLogDetailData();
				//Set changed column name
				auditLogDetailData.setColumnName(method.getAnnotation(Column.class) == null? prop.toUpperCase() : method.getAnnotation(Column.class).name());
				//Set changed column java type
				auditLogDetailData.setColumnType(method.getReturnType().getSimpleName());
				if(operation == LogOperationTypeEnum.INSERT){ //In case of insert
					//Put only a new value with the value in the entity
					Object newValue = field.get(entity);
					auditLogDetailData.setNewValue(newValue==null?null:newValue.toString());
				} else if(operation == LogOperationTypeEnum.UPDATE){//In case of update
					Object oldValue = field.get(oldRow);
					Object newValue = field.get(entity);

					//Set newValue to null if empty string
					if(newValue != null && newValue.toString().length() == 0)
						newValue = null;
					
					
					//Check that the old value is different from the new value (column value changed)
					if((oldValue == null && newValue == null) || 
							(oldValue != null && newValue != null &&  oldValue.equals(newValue))){
						field.setAccessible(false);
						continue;
					}
					
					//Special handling for dates because different formats for same date
					if(method.getReturnType().getSimpleName().equals(Date.class.getSimpleName())){
						if(oldValue != null && newValue != null ){
							try{
					            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS"); 
								oldValue = sdf.parse(oldValue.toString()).toString(); 
								if(oldValue.equals(newValue.toString())){
									field.setAccessible(false);
									continue;
								}
					        } catch(ParseException e){
//					        	throw new BusinessException("error_parsingDates");
					        }
						}
					}
					
					//put the new value with the value in the entity and old value with the one in the database's column
					auditLogDetailData.setOldValue(oldValue==null?null:oldValue.toString());
					auditLogDetailData.setNewValue(newValue==null?null:newValue.toString());
				} else if(operation == LogOperationTypeEnum.DELETE){//In case of delete
					//Put only theold value with the value in the database's column
					Object oldValue = field.get(oldRow);
					auditLogDetailData.setOldValue(oldValue==null?null:oldValue.toString());
				}
				
				//Convert the access modifier for the field to private to disable direct access
				field.setAccessible(false);
				
				//Add the column change detail to the auditLogDetailDataList
				auditLogDetailDataList.add(auditLogDetailData);
			}
			
		} catch (Exception e) { //There are a problem during the send of the log message.
			e.printStackTrace();
			throw new BusinessException("error_logException");
		}
		return auditLogDetailDataList;
	}

}
