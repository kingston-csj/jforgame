package com.kingston.jforgame.merge.service;

import com.kingston.jforgame.merge.config.MergeConfig;
import com.kingston.jforgame.merge.utils.XmlUtils;

public class MergeController {

    private MergeConfig mergeConfig;

    public void doMerge() {
        mergeConfig = XmlUtils.loadXmlConfig("merge.xml", MergeConfig.class);
    }

}
