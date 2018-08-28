package com.karim.examples.java.audit.dal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.karim.examples.java.audit.dal.orm.audit.AuditLogData;

/**
 * @author kabdelkareem
 *	
 * New annotation used to be defined on our ORMs
 * to enable audit on this entity
 * Also can used on entity's properties with enable=false
 * to ignore logging the property in JSON string of {@link AuditLogData#setLogRowDataBefore(String)}
 * and  {@link AuditLogData#setLogRowDataAfter(String)}
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE}) 
@Inherited
public @interface Auditable {
	boolean enabled() default true;
}
