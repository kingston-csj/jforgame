package jforgame.data.reader;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

public interface DataReader {

    /**
     * Converts input stream to record collection
     *
     * @param is    file stream {@link ClassPathResource#getInputStream()}
     * @param clazz configuration class
     * @param <E>   configuration class type
     * @return record collection
     */
    <E> List<E> read(InputStream is, Class<E> clazz);
}
