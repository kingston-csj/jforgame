package jforgame.orm.converter;

/**
 * 实体属性转换器
 * 这里比JPA的AttributeConverter更灵活，因为JPA的AttributeConverter#convertToEntityAttribute方法只包含数据，
 * 没有包含数据类型，在使用json序列化时，会丢失数据类型信息，导致序列化信息比较冗余
 *
 * @param <X>
 * @param <Y>
 */
public interface AttributeConverter<X, Y> {

    /**
     * Converts the value stored in the entity attribute into the
     * data representation to be stored in the database.
     *
     * @param attribute the entity attribute value to be converted
     * @return the converted data to be stored in the database
     * column
     */
    Y convertToDatabaseColumn(X attribute);

    /**
     * Converts the data stored in the database column into the
     * value to be stored in the entity attribute.
     * Note that it is the responsibility of the converter writer to
     * specify the correct <code>dbData</code> type for the corresponding
     * column for use by the JDBC driver: i.e., persistence providers are
     * not expected to do such type conversion.
     *
     * @param clazz  the class of the dbdata
     * @param dbData the data from the database column to be
     *               converted
     * @return the converted value to be stored in the entity
     * attribute
     */
    X convertToEntityAttribute(Class<X> clazz, Y dbData);

}