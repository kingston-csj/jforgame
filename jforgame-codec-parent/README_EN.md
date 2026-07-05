### Ⅰ. Introduction

`jforgame-codec` is a message encoding and decoding module that provides message codec capabilities for private protocol stacks. The module contains one core interface and three implementation methods.

### Ⅱ. Core API

`MessageCodec` is the core interface for message encoding and decoding, defined as follows:

```java
public interface MessageCodec {

    /**
     * Deserialize message based on message metadata
     *
     * @param clazz class of the message
     * @param body  data body of the message
     * @return request message
     */
    Object decode(Class<?> clazz, byte[] body);

    /**
     * Serialize a specific message to byte[]
     * @param message message to encode
     * @return byte array of the message
     */
    byte[] encode(Object message);
}
```

### Ⅲ. Three Implementations

#### 1. JsonCodec (JSON Implementation)

JSON-based encoding and decoding method, the simplest and most practical, especially suitable for lightweight game servers like mini games.

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

**Features:**
- Simple to use, easy to debug
- Good cross-language support
- Moderate performance
- Suitable for small projects or prototype development

#### 2. ProtobufCodec (Protobuf Implementation)

Encoder and decoder based on Google Protocol Buffers, requires adding `@ProtobufClass` annotation on message classes.

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

**Features:**
- High performance, small serialization size
- Multi-language support
- Requires defining `.proto` files
- Can generate proto files via `ProtobufIDLGenerator`
- Suitable for projects with higher performance requirements

#### 3. StructCodec (Struct Implementation)

Custom encoding and decoding based on Bean structure, supports basic types, arrays, collections and custom types.

**Collection Serialization Modes:**

| Mode | Description |
| --- | --- |
| `STRICT_HOMOGENEOUS` | Strict homogeneous mode: all elements must be exactly the same as wrapper type, concise protocol |
| `SUB_CLASS_POLYMORPHIC` | Subclass polymorphic mode: allows elements to be subclass/implementation of wrapper, supports polymorphism |

```java
// Create codec
StructCodec codec = new StructCodec(1024 * 1024); // Specify buffer size

// Encode
byte[] data = codec.encode(messageObject);

// Decode
MessageObj obj = (MessageObj) codec.decode(MessageObj.class, data);
```

**Supported Types:**
- Basic types: boolean, byte, short, int, float, double, long, String
- Array types: Object[]
- Collection types: List, Set
- Map types: Map (Key is forced to String)
- Custom Bean types

**Features:**
- Best performance, high customization
- Binary format, small size
- Requires getter/setter on message classes
- Supports field ignore annotation `@FieldIgnore`
- Suitable for projects pursuing ultimate performance
- Compared to protobuf, collection element types support polymorphism, greatly improving development flexibility

### Ⅳ. Usage Examples

#### Define Message Class

```java
// Struct style
public class PlayerMessage {
    private long playerId;
    private String name;
    private int level;
    // getter/setter...
}
```

#### Choose Codec

```java
// Method 1: Use JSON (simplest)
MessageCodec jsonCodec = new JsonCodec();

// Method 2: Use Protobuf (high performance)
MessageCodec protobufCodec = new ProtobufCodec();

// Method 3: Use Struct (High scalability)
MessageCodec structCodec = new StructCodec();
```

#### Message Encoding and Decoding

```java
// Encode
PlayerMessage message = new PlayerMessage();
message.setPlayerId(1001);
message.setName("test");
message.setLevel(10);

byte[] data = codec.encode(message);

// Decode
PlayerMessage decoded = (PlayerMessage) codec.decode(PlayerMessage.class, data);
```

### Ⅴ. Notes

1. **JSON Implementation**: Depends on `jforgame-codec-json` module
2. **Protobuf Implementation**: Depends on `jforgame-codec-protobuf` module, message classes need `@ProtobufClass` annotation
3. **Struct Implementation**: Depends on `jforgame-codec-struct` module, message classes need no-arg constructor and getter/setter
4. **Field Type Restrictions**:
   - Struct collection elements cannot be parent class or abstract class by default (unless upgrade version is enabled)
   - Map Key type is forced to String