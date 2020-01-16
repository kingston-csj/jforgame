package com.kingston.jforgame.merge.service;

import com.kingston.jforgame.merge.config.MergeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CleanService {

    private static CleanService self = new CleanService();

    private Logger logger = LoggerFactory.getLogger(self.getClass());

    public static CleanService getInstance() {
        return self;
    }

    public void clearRubbish(MergeServer parent, List<MergeServer> children) {
        if (parent != null) {
            clear(parent);
        }
        if (children != null) {
            children.forEach(this::clear);
        }
    }

    private void clear(MergeServer server) {
        logger.error("开始对服务[{}]执行清档逻辑", server.getServerId());
    }

}
