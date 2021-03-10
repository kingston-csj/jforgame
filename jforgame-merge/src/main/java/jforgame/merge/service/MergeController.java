package jforgame.merge.service;

import jforgame.merge.config.MergeConfig;
import jforgame.merge.config.MergeServer;

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
        CleanService.getInstance().clearRubbish(mergeConfig.getParentServer(), mergeConfig.getChildServers());
        // 加载玩家和战盟名字
        RenameService.getInstance().initNamePool(mergeConfig.getParentServer());
        // 真正合服
        for (MergeServer childServer : mergeConfig.getChildServers()) {
            MergeService.getInstance().doMerge(mergeConfig.getParentServer(), childServer);
        }
    }

}
