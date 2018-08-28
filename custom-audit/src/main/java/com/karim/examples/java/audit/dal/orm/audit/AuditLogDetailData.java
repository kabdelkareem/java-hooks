package com.karim.examples.java.audit.dal.orm.audit;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.karim.examples.java.audit.dal.orm.AuditEntity;

/**
 * @author kabdelkareem
 *
 *	Detail Entity contain extra properties about changed 
 *  columns' values only. 
 *  In case of insert/update, all columns logged.
 *  
 */

@Entity
@Table(name="AUDT_LOG_DTLS")
@SuppressWarnings("serial")
public class AuditLogDetailData extends AuditEntity {
	private Long id;
	private String columnName;
	private String columnType;
	private String oldValue;
	private String newValue;
	private Long auditLogId;
	

    @Id 
    @Column(name="ID")
    @SequenceGenerator(name = "SEQ_AUDT_LOG_DTLS", sequenceName = "SEQ_AUDT_LOG_DTLS")
    @GeneratedValue(generator = "SEQ_AUDT_LOG_DTLS")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

    @Basic
    @Column(name="COLUMN_NAME")
    public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

    @Basic
    @Column(name="COLUMN_TYPE")
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

    @Basic
    @Column(name="OLD_VALUE")
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

    @Basic
    @Column(name="NEW_VALUE")
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
    @Basic
    @Column(name="AUDIT_LOG_ID")
	public Long getAuditLogId() {
		return auditLogId;
	}
	public void setAuditLogId(Long auditLogId) {
		this.auditLogId = auditLogId;
	}
}
