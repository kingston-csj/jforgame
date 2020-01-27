package com.kingston.jforgame.merge.service;

import com.kingston.jforgame.merge.config.MergeConfig;
import com.kingston.jforgame.merge.config.MergeServer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MergeController {

    public void doMerge() throws Exception {
        MergeConfig mergeConfig = MergeConfig.getInstance();
        // 备份数据库sql
        if (mergeConfig.isBackup()) {
            MergeServer parent = mergeConfig.getParentServer();
            String backName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".sql";
            BackUpService.getInstance().dbBackUp(parent, "D://", backName);
        }
        // 角色清档
//        CleanService.getInstance().clearRubbish(mergeConfig.getParentServer(), mergeConfig.getChildServers());
        // 真正合服
        for (MergeServer childServer : mergeConfig.getChildServers()) {
            MergeService.getInstance().doMerge(mergeConfig.getParentServer(), childServer);
        }

    }


}
