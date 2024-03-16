package jforgame.commons;

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
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileUtils {

    /**
     * read file content based on Utf-8
     *
     * @param fileName targetFileName
     */
    public static String readFullText(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        FileInputStream in = new FileInputStream(file);
        return readFullText(in);
    }

    public static Stream<String> readLines(String fileName) throws IOException {
        return Files.lines(Paths.get(fileName));
    }

    public static String readFullText(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        // 指定读取文件时以UTF-8的格式读取
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

}
