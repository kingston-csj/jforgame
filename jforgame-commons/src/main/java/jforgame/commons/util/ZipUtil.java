package jforgame.commons.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZIP utility class
 * Provides compression and decompression functions for strings, byte arrays, and files
 */
public class ZipUtil {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String DEFAULT_ZIP_ENTRY_NAME = "data";
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int DEFAULT_COMPRESSION_LEVEL = 6; // 0-9, 6 is the default value

    /**
     * Compresses a string and encodes it with Base64
     *
     * @param input the string to compress
     * @return the Base64 encoded compressed string
     */
    public static String compressString(String input) {
        return compressString(input, DEFAULT_COMPRESSION_LEVEL);
    }

    /**
     * Compresses a string and encodes it with Base64 (with specified compression level)
     *
     * @param input            the string to compress
     * @param compressionLevel compression level (0-9)
     * @return the Base64 encoded compressed string
     */
    public static String compressString(String input, int compressionLevel) {
        if (input == null) {
            return null;
        }
        return DigestUtil.encodeBase64(compressBytes(input.getBytes(DEFAULT_CHARSET), compressionLevel));
    }

    /**
     * Decompresses a Base64 encoded string
     *
     * @param compressed the Base64 encoded compressed string
     * @return the decompressed string
     */
    public static String decompressString(String compressed) {
        if (compressed == null) {
            return null;
        }
        return new String(decompressBytes(DigestUtil.decodeBase64(compressed)), DEFAULT_CHARSET);
    }

    /**
     * Compresses a byte array
     *
     * @param input the byte array to compress
     * @return the compressed byte array
     */
    public static byte[] compressBytes(byte[] input) {
        return compressBytes(input, DEFAULT_COMPRESSION_LEVEL);
    }

    /**
     * Compresses a byte array (with specified compression level)
     *
     * @param input            the byte array to compress
     * @param compressionLevel compression level (0-9)
     * @return the compressed byte array
     */
    public static byte[] compressBytes(byte[] input, int compressionLevel) {
        if (input == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            zos.setLevel(compressionLevel);
            zos.putNextEntry(new ZipEntry(DEFAULT_ZIP_ENTRY_NAME));
            zos.write(input);
            zos.closeEntry();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("IO exception occurred while compressing byte array", e);
        }
    }

    /**
     * Decompresses a byte array
     *
     * @param compressed the compressed byte array
     * @return the decompressed byte array
     */
    public static byte[] decompressBytes(byte[] compressed) {
        if (compressed == null) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
             ZipInputStream zis = new ZipInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                throw new RuntimeException("No valid Zip entry found in compressed data");
            }

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = zis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("IO exception occurred while decompressing byte array", e);
        }
    }

    /**
     * Compresses a file
     *
     * @param sourceFile the source file
     * @param targetFile the target compressed file
     * @throws RuntimeException compression exception
     */
    public static void compressFile(File sourceFile, File targetFile) throws RuntimeException {
        compressFile(sourceFile, targetFile, DEFAULT_COMPRESSION_LEVEL);
    }

    /**
     * Compresses a file (with specified compression level)
     *
     * @param sourceFile       the source file
     * @param targetFile       the target compressed file
     * @param compressionLevel compression level (0-9)
     * @throws RuntimeException compression exception
     */
    public static void compressFile(File sourceFile, File targetFile, int compressionLevel) throws RuntimeException {
        if (sourceFile == null || targetFile == null) {
            throw new RuntimeException("Source file or target file cannot be null");
        }

        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zos.setLevel(compressionLevel);
            ZipEntry entry = new ZipEntry(sourceFile.getName());
            zos.putNextEntry(entry);

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("IO exception occurred while compressing file", e);
        }
    }

    /**
     * Decompresses a file
     *
     * @param sourceFile the source compressed file
     * @param targetDir  the target directory
     * @throws RuntimeException decompression exception
     */
    public static void decompressFile(File sourceFile, File targetDir) throws RuntimeException {
        if (sourceFile == null || targetDir == null) {
            throw new RuntimeException("Source file or target directory cannot be null");
        }

        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new RuntimeException("Unable to create target directory: " + targetDir.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(sourceFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File targetFile = new File(targetDir, entry.getName());

                // Create parent directory
                File parentDir = targetFile.getParentFile();
                if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                    throw new RuntimeException("Unable to create directory: " + parentDir.getAbsolutePath());
                }

                // Write to file
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("IO exception occurred while decompressing file", e);
        }
    }

    /**
     * Gets the compression ratio
     *
     * @param original   the original byte array
     * @param compressed the compressed byte array
     * @return the compression ratio (0.0-1.0)
     */
    public static double getCompressionRatio(byte[] original, byte[] compressed) {
        if (original == null || compressed == null || original.length == 0) {
            return 0.0;
        }
        return 1.0 - ((double) compressed.length / original.length);
    }

}