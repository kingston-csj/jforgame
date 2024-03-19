package jforgame.demo.game.logger;


import org.apache.logging.log4j.Logger;

public enum LoggerSystem {

    EXCEPTION,

    HTTP_COMMAND,

    MONITOR,

    ;

    public Logger getLogger() {
        return LoggerBuilder.getLogger(this.name());
    }

}