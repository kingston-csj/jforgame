package com.kingston.jforgame.merge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergeService {

    private static MergeService self = new MergeService();

    private static Logger logger = LoggerFactory.getLogger(MergeService.class);

    public static MergeService getInstance() {
        return self;
    }
}
