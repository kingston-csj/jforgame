package jforgame.codec.struct;

/**
 * Struct codec configuration environment
 */
public class StructCodecEnvironment {

    /**
     * Serialization mode supported by collection fields, default is strict homogeneous mode
     */
    static CollectionSerializeMode collectionSerializeMode = CollectionSerializeMode.STRICT_HOMOGENEOUS;

    /**
     * Message factory, only needed when collectionSerializeMode is SUB_CLASS_POLYMORPHIC
     */
    public static LiteMessageFactory messageFactory;


}
