package jforgame.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Log4j2 implementation of {@link AppLogger}
 * Provides classified logging with separate files for each logger type
 */
public class Log4J2AppLogger implements AppLogger {

    private static final String LOG_PATTERN = "%m%n";

    private final Logger delegate;

    public Log4J2AppLogger(String name) {
        LoggerBuilder.initialize();
        configure(name);
        this.delegate = LogManager.getLogger(name);
    }

    static void initializeDefaultConfiguration() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        synchronized (context) {
            ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
            builder.setConfigurationName("jforgame");
            builder.setStatusLevel(Level.WARN);
            builder.add(builder.newAppender("Console", "Console").addAttribute("target", "SYSTEM_OUT")
                    .add(builder.newLayout("PatternLayout").addAttribute("pattern", jforgame.logger.LoggerConfig.DEFAULT_PATTERN + "%ex")));
            builder.add(buildRootAppender(builder, "App", jforgame.logger.LoggerConfig.LOG_PATH + "/app.log", false));
            builder.add(buildRootAppender(builder, "Exception", jforgame.logger.LoggerConfig.LOG_PATH + "/exception/exception.log", true));
            builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("Console"))
                    .add(builder.newAppenderRef("App")).add(builder.newAppenderRef("Exception")));
            context.start(builder.build(false));
        }
    }

    private static AppenderComponentBuilder buildRootAppender(ConfigurationBuilder<BuiltConfiguration> builder,
                                                              String name, String file, boolean errorOnly) {
        Path path = Paths.get(file);
        return builder.newAppender(name, "RollingFile").addAttribute("fileName", file)
                .addAttribute("filePattern", file + ".%d{yyyy-MM-dd}")
                .add(builder.newFilter(errorOnly ? "LevelMatchFilter" : "ThresholdFilter",
                        Filter.Result.ACCEPT, Filter.Result.DENY).addAttribute("level", errorOnly ? "ERROR" : "INFO"))
                .add(builder.newLayout("PatternLayout").addAttribute("pattern", jforgame.logger.LoggerConfig.DEFAULT_PATTERN + "%ex"))
                .addComponent(builder.newComponent("Policies").addComponent(builder.newComponent("TimeBasedTriggeringPolicy")
                        .addAttribute("interval", 1).addAttribute("modulate", true)))
                .addComponent(builder.newComponent("DefaultRolloverStrategy")
                        .addComponent(builder.newComponent("Delete").addAttribute("basePath", path.getParent().toString())
                                .addAttribute("maxDepth", 1)
                                .addComponent(builder.newComponent("IfFileName").addAttribute("glob", path.getFileName() + ".*"))
                                .addComponent(builder.newComponent("IfLastModified")
                                        .addAttribute("age", jforgame.logger.LoggerConfig.DEFAULT_HISTORY_DAYS + "D"))));
    }

    /**
     * Configures a dedicated appender for the specified logger
     * Creates a rolling file appender with daily rotation
     *
     * @param name the logger name to configure
     */
    private void configure(String name) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        synchronized (context) {
            Configuration config = context.getConfiguration();
            String appenderName = "CLASSIFY_" + name;
            String filePath = jforgame.logger.LoggerConfig.resolveFilePath(name);
            jforgame.logger.LoggerConfig.RollingPeriod rollingPeriod = jforgame.logger.LoggerConfig.resolveRollingPeriod(name);

            if (config.getAppender(appenderName) == null) {
                PatternLayout layout = PatternLayout.newBuilder()
                        .withConfiguration(config)
                        .withPattern(LOG_PATTERN)
                        .build();
                RollingFileAppender appender = buildRollingAppender(config, appenderName, filePath, layout, rollingPeriod);
                appender.start();
                config.addAppender(appender);
            }

            LoggerConfig loggerConfig = config.getLoggerConfig(name);
            if (!name.equals(loggerConfig.getName())) {
                loggerConfig = new LoggerConfig(name, Level.INFO, false);
                config.addLogger(name, loggerConfig);
            }
            if (!loggerConfig.getAppenders().containsKey(appenderName)) {
                loggerConfig.addAppender(config.getAppender(appenderName), Level.INFO, null);
            }
            loggerConfig.setLevel(Level.INFO);
            loggerConfig.setAdditive(false);
            context.updateLoggers();
        }
    }

    private static RollingFileAppender buildRollingAppender(Configuration config, String appenderName,
                                                            String filePath, PatternLayout layout,
                                                            jforgame.logger.LoggerConfig.RollingPeriod rollingPeriod) {
        TimeBasedTriggeringPolicy policy = TimeBasedTriggeringPolicy.newBuilder()
                .withInterval(1)
                .withModulate(true)
                .build();
        return RollingFileAppender.newBuilder()
                .setConfiguration(config)
                .withName(appenderName)
                .withFileName(filePath)
                .withFilePattern(rollingPeriod.log4j2FilePattern(filePath))
                .withPolicy(policy)
                .withAppend(true)
                .setLayout(layout)
                .build();
    }

    @Override
    public void info(String msg) {
        delegate.info(msg);
    }

    @Override
    public void error(String msg, Throwable throwable) {
        delegate.error(msg, throwable);
    }

    @Override
    public void error(String format, Object... arguments) {
        delegate.error(format, arguments);
    }
}
