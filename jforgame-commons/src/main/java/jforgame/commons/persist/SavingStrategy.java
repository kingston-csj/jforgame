package jforgame.commons.persist;

/**
 * Persistence strategy
 */
public interface SavingStrategy {

    /**
     * Actually execute database work, implementation class can choose persistence method, such as: spring data jpa, jforgame-orm, mybatis etc.
     * When persistence fails, client code must catch this exception, choose to put object back into waiting queue as appropriate, avoid data loss
     * @param entity entity object
     * @throws Exception exception that may be thrown during saving, must be taken seriously!!
     *
     */
    void doSave(Entity<?> entity) throws Exception;


}
