package jforgame.commons.util;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * Data encoding and decoding utility
 * Provides common encoding and decoding functions such as MD5, Base64, etc.
 */
public class DigestUtil {
    // Define hexadecimal character array
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();


    /**
     * Calculates MD5 digest and returns a hexadecimal string
     *
     * @param bytes the input byte array
     * @return the MD5 digest as a hexadecimal string
     */
    public static String md5Hex(byte[] bytes) {
        return digestAsHexString("MD5", bytes);
    }

    /**
     * Calculates MD5 digest and returns a hexadecimal string
     *
     * @param data the input string
     * @return the MD5 digest as a hexadecimal string
     */
    public static String md5Hex(String data) {
        Objects.requireNonNull(data);
        return md5Hex(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Calculates MD5 digest and returns a hexadecimal string
     *
     * @param inputStream the input stream
     * @return the MD5 digest as a hexadecimal string
     * @throws IOException if an I/O error occurs while reading the input stream
     */
    public static String md5Hex(InputStream inputStream) throws IOException {
        return digestAsHexString("MD5", inputStream);
    }

    /**
     * Calculates digest and returns a hexadecimal string
     *
     * @param algorithm the digest algorithm name
     * @param bytes     the input byte array
     * @return the digest as a hexadecimal string
     */
    private static String digestAsHexString(String algorithm, byte[] bytes) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return new String(hexDigest);
    }

    /**
     * Calculates digest and returns a hexadecimal string
     *
     * @param algorithm   the digest algorithm name
     * @param inputStream the input stream
     * @return the digest as a hexadecimal string
     * @throws IOException if an I/O error occurs while reading the input stream
     */
    private static String digestAsHexString(String algorithm, InputStream inputStream) throws IOException {
        char[] hexDigest = digestAsHexChars(algorithm, inputStream);
        return new String(hexDigest);
    }

    /**
     * Calculates digest and returns a hexadecimal character array
     *
     * @param algorithm   the digest algorithm name
     * @param inputStream the input stream
     * @return the digest as a hexadecimal character array
     * @throws IOException if an I/O error occurs while reading the input stream
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
     * Calculates MD5 digest and returns a byte array
     *
     * @param bytes the input byte array
     * @return the MD5 digest as a byte array
     */
    public static byte[] md5Digest(byte[] bytes) {
        return digest("MD5", bytes);
    }

    /**
     * Calculates MD5 digest and returns a byte array
     *
     * @param inputStream the input stream
     * @return the MD5 digest as a byte array
     * @throws IOException if an I/O error occurs while reading the input stream
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
     * Converts a byte array to a Base64 encoded string
     * @param data the input byte array
     * @return the Base64 encoded string
     */
    public static String encodeBase64(byte[] data) {
        return ENCODER.encodeToString(data);
    }

    /**
     * Decodes a Base64 encoded string
     * @param data the Base64 encoded string
     * @return the decoded byte array
     */
    public static byte[] decodeBase64(String data) {
        return DECODER.decode(data);
    }

}
