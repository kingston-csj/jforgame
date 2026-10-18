package jforgame.logger;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;

/** Daily Log4j 1.x appender with age-based cleanup of its own yyyy-MM-dd archives. */
public class RetentionDailyRollingFileAppender extends DailyRollingFileAppender {

    private int maxHistory = 30;
    private LocalDate lastCleanup;

    public void setMaxHistory(int maxHistory) {
        if (maxHistory < 1) {
            throw new IllegalArgumentException("maxHistory must be positive");
        }
        this.maxHistory = maxHistory;
    }

    public int getMaxHistory() {
        return maxHistory;
    }

    @Override
    protected void subAppend(LoggingEvent event) {
        // The parent rolls the active file before we inspect the archives.
        super.subAppend(event);
        LocalDate today = LocalDate.now();
        if (today.equals(lastCleanup) || getFile() == null) {
            return;
        }
        lastCleanup = today;
        Path file = Paths.get(getFile()).toAbsolutePath();
        String prefix = file.getFileName() + ".";
        long cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(maxHistory);
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(file.getParent())) {
            for (Path entry : entries) {
                String name = entry.getFileName().toString();
                if (!name.startsWith(prefix) || !Files.isRegularFile(entry)) {
                    continue;
                }
                try {
                    LocalDate.parse(name.substring(prefix.length()));
                } catch (DateTimeParseException ignored) {
                    continue;
                }
                if (Files.getLastModifiedTime(entry).toMillis() < cutoff) {
                    Files.deleteIfExists(entry);
                }
            }
        } catch (IOException e) {
            LogLog.warn("Cannot clean log archives for " + file, e);
        }
    }
}
