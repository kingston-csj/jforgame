package jforgame.demo.redis;

public interface IRedisSerializer {

	byte[] serialize(Object o);

	<T> T deserialize(byte[] src, Class<T> cls);

}