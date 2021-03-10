package jforgame.merge.service;

import jforgame.merge.config.MergeServer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackUpService {

    private static BackUpService self = new BackUpService();

    public static BackUpService getInstance() {
        return self;
    }

    public void dbBackUp(MergeServer server, String backPath, String backName) throws Exception {
        dbBackUp(server.getUrl(), server.getUrl(), server.getPassword(), server.getDbName(), backPath, backName);
    }

    public void dbBackUp(String url, String root, String pwd, String dbName, String backPath, String backName) throws Exception {
        String pathSql = backPath + dbName + backName;
        File fileSql = new File(pathSql);
        //创建备份sql文件
        if (!fileSql.exists()) {
//            fileSql.createNewFile();
        }
        //   mysqldump -h127.0.0.1 -uroot -p123456 game_user_001 >~/backup/2020-01-17-11-20-11.sql
        StringBuffer sb = new StringBuffer();
        sb.append("mysqldump");
        sb.append(" -h" + url);
        sb.append(" -u" + root);
        sb.append(" -p" + pwd);
        sb.append(" " + dbName + " >");
        sb.append(pathSql);
        System.out.println("cmd命令为：" + sb.toString());
        Runtime runtime = Runtime.getRuntime();
        System.out.println("开始备份：" + dbName);
        // windows  String[] command = { "cmd", "/c", command};
        String[] command = {"/bin/sh", "-c", sb.toString()};
        Process process = runtime.exec(command);
        System.out.println("备份成功!");
    }

    public static void main(String[] args) throws Exception {
        String backName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".sql";
        BackUpService.getInstance().dbBackUp("127.0.0.1", "root", "123456", "game_user_001", "~/backup/", backName);
    }

}
