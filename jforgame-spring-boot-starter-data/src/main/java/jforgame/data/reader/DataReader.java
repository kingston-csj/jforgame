package jforgame.data.reader;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

public interface DataReader {

    /**
     * 将输入流转为记录集合
     *
     * @param is 文件流 {@link ClassPathResource#getInputStream()}
     * @param clazz 配置类class
     * @param <E>
     * @return 记录集合
     */
    <E> List<E> read(InputStream is, Class<E> clazz);
}
