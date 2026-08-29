package jforgame.data.reader;

import jforgame.data.ResourceOptions;
import jforgame.data.TableDefinition;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.List;

/**
 * Default implementation of TableDataLoader, loads configuration data from local file.
 * <p>
 * File path is assembled: location + resourceTable + suffix.
 * Delegates content‑parsing work to FileDataReader.
 * </p>
 */
public class FileTableDataLoader implements TableDataLoader {

    private final DataReader fileDataReader;

    public FileTableDataLoader(DataReader fileDataReader) {
        this.fileDataReader = fileDataReader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> load(TableDefinition definition, ResourceOptions options) throws Exception {
        String filePath = options.getLocation() + definition.getResourceTable() + options.getSuffix();
        Resource resource = new FileSystemResource(filePath);
        try (InputStream is = resource.getInputStream()) {
            return fileDataReader.read(is, definition.getClazz());
        }
    }

}
