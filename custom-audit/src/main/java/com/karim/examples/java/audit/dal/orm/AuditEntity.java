package com.karim.examples.java.audit.dal.orm;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * @author kabdelkareem
 *
 *	The root class of audit entities
 */

@MappedSuperclass
public abstract class AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;
}