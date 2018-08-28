# Custom Audit
Auditing is keeping a record of all changes occurred to the database. For example, if a user updates a record in any table, a record should be added automatically to the auditing log explain who made the changes, what the changes were, and when it occurs. For some systems, this is an absolute requirement.

This example project, logs all changes occur by adding annotation `Auditable` to `BaseEntity` which is the superclass of all of our entities. If you don't know what I am saying, no problem I'll go explain through it step by step.


### Audit Structure (Main Files)
`com.karim.examples.java.audit.dal.orm.audit` contains two ORMs for audit table.

`com.karim.examples.java.audit.dal` contains annotation interface `Auditable.java` to be used in entities which you want it to be logged and `DataAcess.java` which contains the CRUD opreations used to access the database and triggering our audit service.

`com.karim.examples.java.audit.services.audit` contains `AuditService.java` which contain the logging implementation. it use reflection to get entity properties.


### Audit Constraints
* @Table annotation must be defined on entity class.
* @Id, @Column, and @Transient annotations must be used on getter method (not on the property).
* Entities must follow [JavaBean Naming Conventions](https://docstore.mik.ua/orelly/java-ent/jnut/ch06_02.htm).


### Audit Usage Examples
* Audit All Entities Operations (As in our example)
  * Add @Auditable to BaseEntity.
* Audit All Entities Except some entities
  * Add @Auditable to BaseEntity.
  * Add @Auditable(enabled=false) to the entities that you want to exclude.
* Audit some entities only (you can follow one of below ways):
  * Not Many entities to be audited
    * Mark only entities to be audited with @Auditable
  * Many entities to be audited
    * Add @Auditable(enabled=false) to BaseEntity.
    * Mark only entities to be audited with @Auditable
* Audit some attributes in an entity
  * Add @Auditable to entity.
  * Add @Auditable(enabled=false) to the attribute to be execluded from audit
