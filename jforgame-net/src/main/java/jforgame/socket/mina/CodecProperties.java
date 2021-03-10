package jforgame.socket.mina;

/**
 * @author kinson
 */
public class CodecProperties {

	public static final int READ_CAPACITY = 1024;

	public static final int WRITE_CAPACITY = 256;

	/**
	 * 消息元信息常量3表示消息body前面的两个字段，一个short表示module，一个byte表示cmd
	 */
	public static final int MESSAGE_META_SIZE = 3;

}
