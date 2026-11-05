package jforgame.commons.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DigestUtil}
 */
public class DigestUtilTest {

    @Test
    public void testMd5HexString() {
        String md5 = DigestUtil.md5Hex("hello");
        assertNotNull(md5);
        assertEquals(32, md5.length());
        // Known MD5 for "hello"
        assertEquals("5d41402abc4b2a76b9719d911017c592", md5);
    }

    @Test
    public void testMd5HexByteArray() {
        String md5 = DigestUtil.md5Hex("hello".getBytes(StandardCharsets.UTF_8));
        assertEquals("5d41402abc4b2a76b9719d911017c592", md5);
    }

    @Test
    public void testMd5HexEmptyString() {
        String md5 = DigestUtil.md5Hex("");
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", md5);
    }

    @Test
    public void testMd5HexInputStream() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8));
        String md5 = DigestUtil.md5Hex(is);
        assertEquals("5d41402abc4b2a76b9719d911017c592", md5);
    }

    @Test(expected = NullPointerException.class)
    public void testMd5HexNull() {
        DigestUtil.md5Hex((String) null);
    }

    @Test
    public void testMd5Digest() {
        byte[] digest = DigestUtil.md5Digest("hello".getBytes(StandardCharsets.UTF_8));
        assertNotNull(digest);
        assertEquals(16, digest.length);
    }

    @Test
    public void testMd5DigestInputStream() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8));
        byte[] digest = DigestUtil.md5Digest(is);
        assertNotNull(digest);
        assertEquals(16, digest.length);
    }

    @Test
    public void testEncodeBase64() {
        String encoded = DigestUtil.encodeBase64("hello".getBytes(StandardCharsets.UTF_8));
        assertNotNull(encoded);
        assertEquals("aGVsbG8=", encoded);
    }

    @Test
    public void testDecodeBase64() {
        byte[] decoded = DigestUtil.decodeBase64("aGVsbG8=");
        assertEquals("hello", new String(decoded, StandardCharsets.UTF_8));
    }

    @Test
    public void testBase64RoundTrip() {
        String original = "test data for encoding";
        byte[] encoded = DigestUtil.encodeBase64(original.getBytes(StandardCharsets.UTF_8)).getBytes();
        byte[] decoded = DigestUtil.decodeBase64(new String(encoded));
        assertEquals(original, new String(decoded, StandardCharsets.UTF_8));
    }

    @Test
    public void testDifferentInputsDifferentMd5() {
        String md5a = DigestUtil.md5Hex("hello");
        String md5b = DigestUtil.md5Hex("world");
        assertNotEquals(md5a, md5b);
    }

    @Test
    public void testSameInputSameMd5() {
        String md5a = DigestUtil.md5Hex("same");
        String md5b = DigestUtil.md5Hex("same");
        assertEquals(md5a, md5b);
    }
}
