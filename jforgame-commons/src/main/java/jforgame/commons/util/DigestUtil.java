package jforgame.commons.util;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * 数据编解码工具
 * 提供诸如MD5、Base64等常用编码和解码功能。
 */
public class DigestUtil {
    // 定义16进制字符数组
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();


    /**
     * 计算MD5摘要并返回16进制字符串
     *
     * @param bytes 输入字节数组
     * @return 16进制表示的MD5摘要字符串
     */
    public static String md5Hex(byte[] bytes) {
        return digestAsHexString("MD5", bytes);
    }

    /**
     * 计算MD5摘要并返回16进制字符串
     *
     * @param data 输入字符串
     * @return 16进制表示的MD5摘要字符串
     */
    public static String md5Hex(String data) {
        Objects.requireNonNull(data);
        return md5Hex(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 计算MD5摘要并返回16进制字符串
     *
     * @param inputStream 输入流
     * @return 16进制表示的MD5摘要字符串
     * @throws IOException 如果读取输入流时发生IO异常
     */
    public static String md5Hex(InputStream inputStream) throws IOException {
        return digestAsHexString("MD5", inputStream);
    }

    /**
     * 计算摘要并返回16进制字符串
     *
     * @param algorithm 摘要算法名称
     * @param bytes     输入字节数组
     * @return 16进制表示的摘要字符串
     */
    private static String digestAsHexString(String algorithm, byte[] bytes) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return new String(hexDigest);
    }

    /**
     * 计算摘要并返回16进制字符串
     *
     * @param algorithm 摘要算法名称
     * @param inputStream 输入流
     * @return 16进制表示的摘要字符串
     * @throws IOException 如果读取输入流时发生IO异常
     */
    private static String digestAsHexString(String algorithm, InputStream inputStream) throws IOException {
        char[] hexDigest = digestAsHexChars(algorithm, inputStream);
        return new String(hexDigest);
    }

    /**
     * 计算摘要并返回16进制字符数组
     *
     * @param algorithm 摘要算法名称
     * @param inputStream 输入流
     * @return 16进制表示的摘要字符数组
     * @throws IOException 如果读取输入流时发生IO异常
     */
    private static char[] digestAsHexChars(String algorithm, InputStream inputStream) throws IOException {
        byte[] digest = digest(algorithm, inputStream);
        return encodeHex(digest);
    }

    private static char[] digestAsHexChars(String algorithm, byte[] bytes) {
        byte[] digest = digest(algorithm, bytes);
        return encodeHex(digest);
    }

    private static char[] encodeHex(byte[] bytes) {
        char[] chars = new char[32];

        for (int i = 0; i < chars.length; i += 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[b >>> 4 & 15];
            chars[i + 1] = HEX_CHARS[b & 15];
        }

        return chars;
    }

    /**
     * 计算MD5摘要并返回字节数组
     *
     * @param bytes 输入字节数组
     * @return MD5摘要字节数组
     */
    public static byte[] md5Digest(byte[] bytes) {
        return digest("MD5", bytes);
    }

    /**
     * 计算MD5摘要并返回字节数组
     *
     * @param inputStream 输入流
     * @return MD5摘要字节数组
     * @throws IOException 如果读取输入流时发生IO异常
     */
    public static byte[] md5Digest(InputStream inputStream) throws IOException {
        return digest("MD5", inputStream);
    }

    private static byte[] digest(String algorithm, byte[] bytes) {
        return getDigest(algorithm).digest(bytes);
    }

    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            NoSuchAlgorithmException ex = var2;
            throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", ex);
        }
    }

    private static byte[] digest(String algorithm, InputStream inputStream) throws IOException {
        MessageDigest messageDigest = getDigest(algorithm);
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, bytesRead);
        }
        return messageDigest.digest();
    }


    /**
     * 将字节数组转为Base64编码字符串
     * @param data 输入字节数组
     * @return Base64编码后的字符串
     */
    public static String encodeBase64(byte[] data) {
        return ENCODER.encodeToString(data);
    }

    /**
     * 将字符串进行Base64解码
     * @param data Base64编码后的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decodeBase64(String data) {
        return DECODER.decode(data);
    }

}
