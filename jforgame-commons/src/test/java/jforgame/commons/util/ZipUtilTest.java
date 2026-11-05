package jforgame.commons.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ZipUtil}
 */
public class ZipUtilTest {

    private File tempFile;
    private File tempDir;
    private File outputZip;
    private File extractDir;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("zipTest").toFile();
        tempFile = new File(tempDir, "test.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Hello, this is test content for zipping!");
        }
        outputZip = new File(tempDir, "test.zip");
        extractDir = new File(tempDir, "extracted");
    }

    @After
    public void tearDown() throws IOException {
        // Cleanup
        deleteRecursively(tempDir);
    }

    private void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    @Test
    public void testCompressString() {
        String original = "This is a test string for compression!";
        String compressed = ZipUtil.compressString(original);
        assertNotNull(compressed);
        assertNotEquals(original, compressed);

        String decompressed = ZipUtil.decompressString(compressed);
        assertEquals(original, decompressed);
    }

    @Test
    public void testCompressStringNull() {
        assertNull(ZipUtil.compressString(null));
        assertNull(ZipUtil.decompressString(null));
    }

    @Test
    public void testCompressStringEmpty() {
        String compressed = ZipUtil.compressString("");
        assertNotNull(compressed);
        String decompressed = ZipUtil.decompressString(compressed);
        assertEquals("", decompressed);
    }

    @Test
    public void testCompressStringWithLevel() {
        String original = "Test string with different compression levels";
        String compressed = ZipUtil.compressString(original, 9); // Maximum compression
        assertNotNull(compressed);
        String decompressed = ZipUtil.decompressString(compressed);
        assertEquals(original, decompressed);
    }

    @Test
    public void testCompressBytes() {
        byte[] original = "Hello World!".getBytes(StandardCharsets.UTF_8);
        byte[] compressed = ZipUtil.compressBytes(original);
        assertNotNull(compressed);

        byte[] decompressed = ZipUtil.decompressBytes(compressed);
        assertArrayEquals(original, decompressed);
    }

    @Test
    public void testCompressBytesNull() {
        assertNull(ZipUtil.compressBytes(null));
        assertNull(ZipUtil.decompressBytes(null));
    }

    @Test
    public void testCompressFile() {
        ZipUtil.compressFile(tempFile, outputZip);
        assertTrue(outputZip.exists());
        assertTrue(outputZip.length() > 0);
    }

    @Test(expected = RuntimeException.class)
    public void testCompressFileNull() {
        ZipUtil.compressFile(null, outputZip);
    }

    @Test
    public void testDecompressFile() throws IOException {
        ZipUtil.compressFile(tempFile, outputZip);
        ZipUtil.decompressFile(outputZip, extractDir);

        assertTrue(extractDir.exists());
        File extractedFile = new File(extractDir, tempFile.getName());
        assertTrue(extractedFile.exists());

        // Verify content
        String content = new String(Files.readAllBytes(extractedFile.toPath()), StandardCharsets.UTF_8);
        assertEquals("Hello, this is test content for zipping!", content);
    }

    @Test(expected = RuntimeException.class)
    public void testDecompressFileNull() {
        ZipUtil.decompressFile(null, extractDir);
    }

    @Test
    public void testGetCompressionRatio() {
        byte[] original = new byte[1000];
        for (int i = 0; i < original.length; i++) {
            original[i] = 0; // Highly compressible
        }
        byte[] compressed = ZipUtil.compressBytes(original);

        double ratio = ZipUtil.getCompressionRatio(original, compressed);
        assertTrue(ratio > 0);
        assertTrue(ratio < 1);
    }

    @Test
    public void testGetCompressionRatioNull() {
        assertEquals(0.0, ZipUtil.getCompressionRatio(null, new byte[10]), 0.001);
        assertEquals(0.0, ZipUtil.getCompressionRatio(new byte[10], null), 0.001);
    }

    @Test
    public void testGetCompressionRatioEmpty() {
        assertEquals(0.0, ZipUtil.getCompressionRatio(new byte[0], new byte[10]), 0.001);
    }

    @Test
    public void testRoundTripLargeData() {
        // Generate some data
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Test data line ").append(i).append("\n");
        }
        String original = sb.toString();

        String compressed = ZipUtil.compressString(original);
        assertNotNull(compressed);

        String decompressed = ZipUtil.decompressString(compressed);
        assertEquals(original, decompressed);
    }
}
