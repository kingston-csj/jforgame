package jforgame.codec.struct;

/**
 * Serialization mode for collection fields in communication protocol objects, includes the following types
 *
 * @see ArrayCodec
 * @see CollectionCodec
 * @see MapCodec
 */
enum CollectionSerializeMode {

    /**
     * Strict homogeneous mode: all elements must be exactly the same as wrapper type, subclasses/heterogeneous types are not allowed, protocol is concise (status=0, no messageId)
     */
    STRICT_HOMOGENEOUS,
    /**
     * Subclass polymorphic mode: allows elements to be subclass/implementation of wrapper, supports polymorphic restoration (status can be 1, each element writes messageId separately)
     */
    SUB_CLASS_POLYMORPHIC

}
