package com.karim.examples.java.audit.dal.orm;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import com.karim.examples.java.audit.dal.Auditable;

/**
 * @author kabdelkareem
 *
 *	the root class of non-audit entities.
 *	{@link Auditable} defined to {@link BaseEntity} to allow audit on all child entities. 
 */
@MappedSuperclass
@Auditable
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public abstract void setId(Long id);
}