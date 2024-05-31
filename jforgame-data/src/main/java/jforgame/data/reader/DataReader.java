package jforgame.data.reader;

import java.io.InputStream;
import java.util.List;

public interface DataReader {

    /**
     * 将输入流转为bean集合
     *
     * @param is
     * @param clazz
     * @param <E>
     * @return
     */
    <E> List<E> read(InputStream is, Class<E> clazz);
}
