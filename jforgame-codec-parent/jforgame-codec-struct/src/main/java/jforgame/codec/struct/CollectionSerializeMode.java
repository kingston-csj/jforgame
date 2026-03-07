package jforgame.codec.struct;

/**
 * 对通信协议对象的集合字段采用的序列化方式， 涉及以下几种类型
 *
 * @see ArrayCodec
 * @see CollectionCodec
 * @see MapCodec
 */
enum CollectionSerializeMode {

    /**
     * 严格同构模式：所有元素必须与wrapper类型完全一致，不允许子类/异构，协议简洁（status=0，无messageId）
     */
    STRICT_HOMOGENEOUS,
    /**
     * 子类继承多态模式：允许元素为wrapper的子类/实现类，支持多态还原（status可为1，每个元素单独写入messageId）
     */
    SUB_CLASS_POLYMORPHIC

}
