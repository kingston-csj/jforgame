package jforgame.commons;

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
 * ZIP工具类
 * 提供字符串、字节数组、文件的压缩和解压缩功能
 */
public class ZipUtil {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String DEFAULT_ZIP_ENTRY_NAME = "data";
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int DEFAULT_COMPRESSION_LEVEL = 6; // 0-9, 6为默认值

    /**
     * 压缩字符串并Base64编码
     *
     * @param input 要压缩的字符串
     * @return Base64编码的压缩字符串
     */
    public static String compressString(String input) {
        return compressString(input, DEFAULT_COMPRESSION_LEVEL);
    }

    /**
     * 压缩字符串并Base64编码（指定压缩级别）
     *
     * @param input            要压缩的字符串
     * @param compressionLevel 压缩级别 (0-9)
     * @return Base64编码的压缩字符串
     */
    public static String compressString(String input, int compressionLevel) {
        if (input == null) {
            return null;
        }
        return DigestUtil.encodeBase64(compressBytes(input.getBytes(DEFAULT_CHARSET), compressionLevel));
    }

    /**
     * 解压缩Base64编码的字符串
     *
     * @param compressed Base64编码的压缩字符串
     * @return 解压缩后的字符串
     */
    public static String decompressString(String compressed) {
        if (compressed == null) {
            return null;
        }
        return new String(decompressBytes(DigestUtil.decodeBase64(compressed)), DEFAULT_CHARSET);
    }

    /**
     * 压缩字节数组
     *
     * @param input 要压缩的字节数组
     * @return 压缩后的字节数组
     */
    public static byte[] compressBytes(byte[] input) {
        return compressBytes(input, DEFAULT_COMPRESSION_LEVEL);
    }

    /**
     * 压缩字节数组（指定压缩级别）
     *
     * @param input            要压缩的字节数组
     * @param compressionLevel 压缩级别 (0-9)
     * @return 压缩后的字节数组
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
            throw new RuntimeException("压缩字节数组时发生IO异常", e);
        }
    }

    /**
     * 解压缩字节数组
     *
     * @param compressed 压缩的字节数组
     * @return 解压缩后的字节数组
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
                throw new RuntimeException("压缩数据中没有找到有效的Zip条目");
            }

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = zis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("解压缩字节数组时发生IO异常", e);
        }
    }

    /**
     * 压缩文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标压缩文件
     * @throws RuntimeException 压缩异常
     */
    public static void compressFile(File sourceFile, File targetFile) throws RuntimeException {
        compressFile(sourceFile, targetFile, DEFAULT_COMPRESSION_LEVEL);
    }

    /**
     * 压缩文件（指定压缩级别）
     *
     * @param sourceFile       源文件
     * @param targetFile       目标压缩文件
     * @param compressionLevel 压缩级别 (0-9)
     * @throws RuntimeException 压缩异常
     */
    public static void compressFile(File sourceFile, File targetFile, int compressionLevel) throws RuntimeException {
        if (sourceFile == null || targetFile == null) {
            throw new RuntimeException("源文件或目标文件不能为空");
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
            throw new RuntimeException("压缩文件时发生IO异常", e);
        }
    }

    /**
     * 解压缩文件
     *
     * @param sourceFile 源压缩文件
     * @param targetDir  目标目录
     * @throws RuntimeException 解压缩异常
     */
    public static void decompressFile(File sourceFile, File targetDir) throws RuntimeException {
        if (sourceFile == null || targetDir == null) {
            throw new RuntimeException("源文件或目标目录不能为空");
        }

        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new RuntimeException("无法创建目标目录: " + targetDir.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(sourceFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File targetFile = new File(targetDir, entry.getName());

                // 创建父目录
                File parentDir = targetFile.getParentFile();
                if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                    throw new RuntimeException("无法创建目录: " + parentDir.getAbsolutePath());
                }

                // 写入文件
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
            throw new RuntimeException("解压缩文件时发生IO异常", e);
        }
    }

    /**
     * 获取压缩比率
     *
     * @param original   原始字节数组
     * @param compressed 压缩后的字节数组
     * @return 压缩比率 (0.0-1.0)
     */
    public static double getCompressionRatio(byte[] original, byte[] compressed) {
        if (original == null || compressed == null || original.length == 0) {
            return 0.0;
        }
        return 1.0 - ((double) compressed.length / original.length);
    }

}