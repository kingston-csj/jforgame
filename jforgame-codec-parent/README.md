### Ⅰ. 简介

`jforgame-codec` 是消息编解码模块，提供私有协议栈的消息编解码能力。模块包含一个核心接口和三种实现方式。

### Ⅱ. 核心API

`MessageCodec` 是消息编解码的核心接口，定义如下：

```java
public interface MessageCodec {

    /**
     * 根据消息元信息反序列号为消息
     *
     * @param clazz class of the message
     * @param body  data body of the message
     * @return request message
     */
    Object decode(Class<?> clazz, byte[] body);

    /**
     * 把一个具体的消息序列化byte[]
     * @param message message to encode
     * @return byte array of the message
     */
    byte[] encode(Object message);
}
```

### Ⅲ. 三种实现

#### 1. JsonCodec（JSON实现）

基于JSON的编解码方式，最为简单实用，特别适用于小游戏等轻量级游戏服务器。

```java
public class JsonCodec implements MessageCodec {
    @Override
    public Object decode(Class<?> clazz, byte[] body) {
        return JsonUtil.string2Object(new String(body, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public byte[] encode(Object message) {
        String json = JsonUtil.object2String(message);
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
```

**特点：**
- 使用简单，调试方便
- 跨语言支持好
- 性能适中
- 适用于小型项目或原型开发

#### 2. ProtobufCodec（Protobuf实现）

基于Google Protocol Buffers的编解码器，需要在消息类上添加 `@ProtobufClass` 注解。

```java
public class ProtobufCodec implements MessageCodec {

    @Override
    public Object decode(Class<?> msgClazz, byte[] body) {
        Codec<?> codec = ProtobufProxy.create(msgClazz);
        return codec.decode(body);
    }

    @Override
    public byte[] encode(Object message) {
        Codec<Object> codec = ProtobufProxy.create(message.getClass());
        return codec.encode(message);
    }
}
```

**特点：**
- 高性能，序列化体积小
- 支持多语言
- 需要定义 `.proto` 文件
- 可通过 `ProtobufIDLGenerator` 生成proto文件
- 适用于对性能要求较高的项目

#### 3. StructCodec（结构体实现）

基于Bean结构体的自定义编解码方式，支持基本类型、数组、集合和自定义类型。

**集合序列化模式：**

| 模式 | 说明 |
| --- | --- |
| `STRICT_HOMOGENEOUS` | 严格同构模式：所有元素必须与wrapper类型完全一致，协议简洁 |
| `SUB_CLASS_POLYMORPHIC` | 子类多态模式：允许元素为wrapper的子类/实现类，支持多态 |

```java
// 创建编解码器
StructCodec codec = new StructCodec(1024 * 1024); // 指定缓冲区大小

// 编码
byte[] data = codec.encode(messageObject);

// 解码
MessageObj obj = (MessageObj) codec.decode(MessageObj.class, data);
```

**支持的类型：**
- 基本类型：boolean, byte, short, int, float, double, long, String
- 数组类型：Object[]
- 集合类型：List, Set
- Map类型：Map（Key强制为String）
- 自定义Bean类型

**特点：**
- 性能最优，定制化程度高
- 二进制格式，体积小
- 需要在消息类上添加getter/setter
- 支持字段忽略注解 `@FieldIgnore`
- 适用于对性能极致追求的项目
- 相对protobuf而已，集合元素类型支持多态，能极大提高开发的灵活性

### Ⅳ. 使用示例

#### 定义消息类

```java
// Struct方式
public class PlayerMessage {
    private long playerId;
    private String name;
    private int level;
    // getter/setter...
}
```

#### 选择编解码器

```java
// 方式一：使用JSON（最简单）
MessageCodec jsonCodec = new JsonCodec();

// 方式二：使用Protobuf（高性能）
MessageCodec protobufCodec = new ProtobufCodec();

// 方式三：使用Struct（最高性能）
MessageCodec structCodec = new StructCodec();
```

#### 消息编解码

```java
// 编码
PlayerMessage message = new PlayerMessage();
message.setPlayerId(1001);
message.setName("test");
message.setLevel(10);

byte[] data = codec.encode(message);

// 解码
PlayerMessage decoded = (PlayerCodec) codec.decode(PlayerMessage.class, data);
```

### Ⅴ. 注意事项

1. **JSON实现**：依赖 `jforgame-codec-json` 模块
2. **Protobuf实现**：依赖 `jforgame-codec-protobuf` 模块，消息类需要添加 `@ProtobufClass` 注解
3. **Struct实现**：依赖 `jforgame-codec-struct` 模块，消息类需要有无参构造器和getter/setter
4. **字段类型限制**：
   - Struct的集合元素默认不能是父类或抽象类（除非启用升级版本）
   - Map的Key类型强制为String