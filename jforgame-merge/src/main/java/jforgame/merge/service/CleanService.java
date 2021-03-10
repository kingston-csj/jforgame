package jforgame.merge.service;

import jforgame.merge.config.MergeConfig;
import jforgame.merge.config.MergeServer;
import jforgame.merge.utils.JdbcUtils;
import jforgame.merge.utils.SqlFactory;
import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CleanService {

    private static CleanService self = new CleanService();

    private Logger logger = LoggerFactory.getLogger(CleanService.class);

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
        MergeConfig config = MergeConfig.getInstance();
        String sql = SqlFactory.createClearPlayerSql(config.getClear().getMinLevel(), config.getClear().getOfflineDays());
        logger.info("清档sql为 {}", sql);

        Connection targetConn = JdbcUtils.getConnection(server);
        int beforeCount = JdbcUtils.queryRecordSum(targetConn, "SELECT COUNT(1) FROM t_role;");
        logger.info("清档前[{}]服角色数量为{}", server.getServerId(), beforeCount);
        int afterCount = JdbcUtils.queryRecordSum(targetConn, "SELECT COUNT(1) FROM t_role;");
        logger.info("清档前[{}]角色数量为{}", server.getServerId(), afterCount);
        List<String> clearTables = MergedTableRegistry.getInstance().listToDeleteTables();

        for (String table : clearTables) {
            logger.info("清空[{}]服【{}】表数据", server.getServerId(), table);
            String delSql = SqlFactory.createDeleteTableSql(table);
            JdbcUtils.execUpdate(targetConn, delSql);
        }
        try {
            DbUtils.close(targetConn);
        } catch (SQLException e) {
            logger.error("", e);
            throw new RuntimeException("");
        }
    }

}
