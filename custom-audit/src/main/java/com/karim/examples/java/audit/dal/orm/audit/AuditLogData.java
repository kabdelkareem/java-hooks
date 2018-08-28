package com.karim.examples.java.audit.dal.orm.audit;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.karim.examples.java.audit.dal.orm.AuditEntity;
import com.karim.examples.java.audit.enums.LogOperationTypeEnum;

/**
 * @author kabdelkareem
 *
 *	Master entity of Log contains all needed features
 *	Note: 
 *		1. logRowDataBefore and logRowDataAfter will contain
 *			JSON string of the object including transient properties.
 *		2. logRowDataBefore and logRowDataAfter can be removed
 *			and be satisfied by using {@link AuditLogDetailData}.
 *
 */
@Entity
@Table(name="AUDT_LOGS")
@SuppressWarnings("serial")
public class AuditLogData extends AuditEntity {
	private Long id;
	private String logTable;
	private LogOperationTypeEnum operationType;
	private String logRowId;
	private String logRowDataBefore;
	private String logRowDataAfter;
	private String moduleCode;
	private String userId;
	private Date operationDate;

	
	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "SEQ_AUDT_LOGS", sequenceName = "SEQ_AUDT_LOGS")
	@GeneratedValue(generator = "SEQ_AUDT_LOGS")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

    @Basic
    @Column(name="LOG_TABLE")
	public String getLogTable() {
		return logTable;
	}
	public void setLogTable(String logTable) {
		this.logTable = logTable;
	}
	
    @Basic
    @Column(name="OPERATION_TYPE")
    @Enumerated(EnumType.ORDINAL)
    public LogOperationTypeEnum getOperationType() {
		return operationType;
	}
	public void setOperationType(LogOperationTypeEnum operationType) {
		this.operationType = operationType;
	}

    @Basic
    @Column(name="LOG_ROW_ID")
	public String getLogRowId() {
		return logRowId;
	}
	public void setLogRowId(String logRowId) {
		this.logRowId = logRowId;
	}

    @Basic
    @Column(name="LOG_ROW_DATA_BEFORE")
	public String getLogRowDataBefore() {
		return logRowDataBefore;
	}
	public void setLogRowDataBefore(String logRowDataBefore) {
		this.logRowDataBefore = logRowDataBefore;
	}

    @Basic
    @Column(name="LOG_ROW_DATA_AFTER")
	public String getLogRowDataAfter() {
		return logRowDataAfter;
	}
	public void setLogRowDataAfter(String logRowDataAfter) {
		this.logRowDataAfter = logRowDataAfter;
	}

    @Basic
    @Column(name="MODULE_CODE")
	public String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

    @Basic
    @Column(name="USER_ID")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
    @Basic
    @Column(name="OPERATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}
}
