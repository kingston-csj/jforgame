package jforgame.codec.struct;

/**
 * 结构体编解码配置环境
 */
public class StructCodecEnvironment {

    /**
     * 集合字段支持的序列化模式，默认为严格同构模式
     */
    static CollectionSerializeMode collectionSerializeMode = CollectionSerializeMode.STRICT_HOMOGENEOUS;

    /**
     * 消息工厂，仅当 collectionSerializeMode为SUB_CLASS_POLYMORPHIC才需要
     */
    public static LiteMessageFactory messageFactory;


}
