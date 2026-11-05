package jforgame.commons.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link FileUtil}
 */
public class FileUtilTest {

    private File tempFile;
    private File tempDir;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("fileUtilTest").toFile();
        tempFile = new File(tempDir, "test.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Hello World\nSecond Line\nThird Line");
        }
    }

    @After
    public void tearDown() throws IOException {
        if (tempFile.exists()) {
            tempFile.delete();
        }
        if (tempDir.exists()) {
            tempDir.delete();
        }
    }

    @Test
    public void testReadFullText() throws IOException {
        String content = FileUtil.readFullText(tempFile.getAbsolutePath());
        assertNotNull(content);
        assertTrue(content.contains("Hello World"));
        assertTrue(content.contains("Second Line"));
        assertTrue(content.contains("Third Line"));
    }

    @Test(expected = java.io.FileNotFoundException.class)
    public void testReadFullTextNonExistent() throws IOException {
        FileUtil.readFullText("/nonexistent/path/file.txt");
    }

    @Test
    public void testReadFullTextInputStream() throws IOException {
        java.io.FileInputStream fis = new java.io.FileInputStream(tempFile);
        String content = FileUtil.readFullText(fis);
        assertNotNull(content);
        assertTrue(content.contains("Hello World"));
        fis.close();
    }

    @Test
    public void testReadLines() throws IOException {
        Stream<String> lines = FileUtil.readLines(tempFile.getAbsolutePath());
        assertNotNull(lines);
        long count = lines.count();
        assertEquals(3, count);
    }

    @Test
    public void testCheckAndCreateDirectory() {
        File newDir = new File(tempDir, "subdir1/subdir2");
        assertFalse(newDir.exists());

        boolean result = FileUtil.checkAndCreateDirectory(newDir.getAbsolutePath());
        assertTrue(result);
        assertTrue(newDir.exists());
        assertTrue(newDir.isDirectory());
    }

    @Test
    public void testCheckAndCreateDirectoryExisting() {
        boolean result = FileUtil.checkAndCreateDirectory(tempDir.getAbsolutePath());
        assertTrue(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckAndCreateDirectoryEmpty() {
        FileUtil.checkAndCreateDirectory("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckAndCreateDirectoryNull() {
        FileUtil.checkAndCreateDirectory(null);
    }

    @Test
    public void testCheckAndCreateDirectoryBlank() {
        try {
            FileUtil.checkAndCreateDirectory("   ");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
