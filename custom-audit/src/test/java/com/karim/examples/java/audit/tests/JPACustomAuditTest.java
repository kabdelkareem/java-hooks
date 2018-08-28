package com.karim.examples.java.audit.tests;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.karim.examples.java.audit.MockVaraibles;
import com.karim.examples.java.audit.dal.DataAccess;
import com.karim.examples.java.audit.dal.orm.audit.AuditLogData;
import com.karim.examples.java.audit.dal.orm.audit.AuditLogDetailData;
import com.karim.examples.java.audit.dal.orm.test.TestEntity;
import com.karim.examples.java.audit.enums.LogOperationTypeEnum;
import com.karim.examples.java.audit.services.test.TestService;

public class JPACustomAuditTest {

    
    
    @BeforeClass
    public static void init() {
        DataAccess.init();
        MockVaraibles.CURRENT_USER.set("Karim");
        
    }
    

	@Test
    @SuppressWarnings("unchecked")
    public void test_A_creating_success() throws Throwable {
		TestEntity testObject = new TestEntity();
		
        // Default Data
        testObject.setId(1L);
        testObject.setName("Test Function");
        testObject.setIgnoreProperty(1);
		
        TestService.insertTestEntity(testObject);
        
        // Test that main record added successfully
        List<TestEntity> testEntityRecords = 
        		(List<TestEntity>) DataAccess.executeHqlQuery("select r from TestEntity r", null);
        assertTrue("Test object not saved successfully", testEntityRecords.size() == 1);
        
        // Test that Audit Log added successfully
        List<AuditLogData> auditLogRecords = 
        		(List<AuditLogData>) DataAccess.executeHqlQuery("select r from AuditLogData r", null);
        assertTrue("Audit Log of saved record not added successfully", auditLogRecords.size() == 1);
        assertTrue(auditLogRecords.get(0).getLogTable().equals("TST_ENTITY"));
        assertTrue(auditLogRecords.get(0).getLogRowId().equals("ID=1"));
        assertTrue(auditLogRecords.get(0).getOperationType().equals(LogOperationTypeEnum.INSERT));
        assertNull(auditLogRecords.get(0).getLogRowDataBefore());
        assertTrue(auditLogRecords.get(0).getLogRowDataAfter().equals("{\"id\":1,\"name\":\"Test Function\"}"));
        
        // Test that Audit Log added successfully
        List<AuditLogDetailData> auditLogDtlRecords = 
        		(List<AuditLogDetailData>) DataAccess.executeHqlQuery("select r from AuditLogDetailData r", null);
        assertTrue("Audit Log details of saved record not added successfully", auditLogDtlRecords.size() == 3);
        
        
        testObject.setName("Name updated...");
        TestService.updateTestEntity(testObject);
        
        // Test that main record updated successfully
        testObject = (TestEntity) DataAccess.executeHqlQuery("select r from TestEntity r", null).get(0);
        assertTrue(testObject.getName().equals("Name updated..."));
        

        // Test that Audit Log added successfully
        auditLogRecords = (List<AuditLogData>) DataAccess
        		.executeHqlQuery("select r from AuditLogData r where operationType = 1 "  , null);
        assertTrue("Audit Log of updated record not added successfully", auditLogRecords.size() == 1);
        assertTrue(auditLogRecords.get(0).getLogTable().equals("TST_ENTITY"));
        assertTrue(auditLogRecords.get(0).getLogRowId().equals("ID=1"));
        assertTrue(auditLogRecords.get(0).getOperationType().equals(LogOperationTypeEnum.UPDATE));
        assertTrue(auditLogRecords.get(0).getLogRowDataBefore().equals("{\"id\":1,\"name\":\"Test Function\"}"));
        assertTrue(auditLogRecords.get(0).getLogRowDataAfter().equals("{\"id\":1,\"name\":\"Name updated...\"}"));
        
        // Test that Audit Log after update one column
        auditLogDtlRecords = 
        		(List<AuditLogDetailData>) DataAccess.executeHqlQuery("select r from AuditLogDetailData r", null);
        assertTrue("Audit Log details of updated record not added successfully", auditLogDtlRecords.size() == 4);
        
    }
	
    
    @AfterClass
    public static void tearDown(){
        DataAccess.close();
    }
}
