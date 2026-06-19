package jforgame.commons.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * File utility class
 */
public final class FileUtil {

    private FileUtil() {

    }

    /**
     * read file content based on Utf-8
     *
     * @param fileName targetFileName
     * @return text content of the file
     * @throws IOException if an I/O error occurs when opening the file
     */
    public static String readFullText(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        FileInputStream in = new FileInputStream(file);
        return readFullText(in);
    }

    /**
     * Read all lines from a file as a Stream. Bytes from the file are decoded into characters using the UTF-8 charset.
     *
     * @param fileName the name of the file
     * @return the lines from the file as a Stream
     * @throws IOException if an I/O error occurs when opening the file
     */
    public static Stream<String> readLines(String fileName) throws IOException {
        return Files.lines(Paths.get(fileName));
    }

    /**
     * read file content based on Utf-8
     *
     * @param inputStream targetInputStream
     * @return text content of the file
     */
    public static String readFullText(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        // Specify reading the file in UTF-8 format
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    /**
     * Checks if a directory exists, creates it if it doesn't (supports multi-level directories)
     *
     * @param directoryPath the directory path (absolute path or relative path)
     * @return true: the directory already exists or was created successfully; false: creation failed (e.g., insufficient permissions)
     */
    public static boolean checkAndCreateDirectory(String directoryPath) {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            throw new IllegalArgumentException("directoryPath cannot be empty");
        }

        File directory = new File(directoryPath);
        // Check if the directory already exists
        if (directory.exists()) {
            return directory.isDirectory();
        } else {
            // Directory does not exist, create it (supports creating multi-level directories)
            return directory.mkdirs();
        }
    }

}
