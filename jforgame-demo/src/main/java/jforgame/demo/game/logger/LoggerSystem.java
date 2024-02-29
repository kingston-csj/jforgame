package jforgame.demo.game.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LoggerSystem {

    EXCEPTION,

    HTTP_COMMAND,

    MONITOR,

    ;

    public Logger getLogger() {
        return LoggerFactory.getLogger(this.name());
    }

}