package com.karim.examples.java.audit.dal.orm.test;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.karim.examples.java.audit.dal.Auditable;
import com.karim.examples.java.audit.dal.orm.BaseEntity;

@Entity
@Table(name="TST_ENTITY")
public class TestEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    private Long id;
     
    private String name;
    
    private Integer ignoreProperty;


	@Id
	@Column(name = "ID")
	public Long getId() {
		return id;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Basic
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "IGNORE_COLUMN")
	@Auditable(enabled=false)
	public Integer getIgnoreProperty() {
		return ignoreProperty;
	}

	public void setIgnoreProperty(Integer ignoreProperty) {
		this.ignoreProperty = ignoreProperty;
	}
    
	

}
