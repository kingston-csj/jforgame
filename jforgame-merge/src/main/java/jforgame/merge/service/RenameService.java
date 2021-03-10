package jforgame.merge.service;

import jforgame.merge.config.MergeServer;
import jforgame.merge.utils.JdbcUtils;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RenameService {

    private static RenameService self = new RenameService();

    public static RenameService getInstance() {
        return self;
    }

    /**
     * 已使用的角色名称
     */
    private Set<String> usedPlayerNames = new HashSet<>();
    /**
     * 已使用的战盟名称
     */
    private Set<String> usedGuildNames = new HashSet<>();

    private AtomicInteger nameIdFactory = new AtomicInteger(0);

    public void initNamePool(MergeServer parentServer) {
        Connection conn = JdbcUtils.getConnection(parentServer);
        ResultSet rs = null;
        try {
            rs = conn.createStatement().executeQuery("SELECT name FROM t_role");
            Set<String> playerNames = new HashSet<>();
            while (rs.next()) {
                playerNames.add(rs.getString("name"));
            }
            usedPlayerNames = playerNames;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
        }
        try {
            rs = conn.createStatement().executeQuery("SELECT name as name FROM t_party");
            Set<String> names = new HashSet<>();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
            usedGuildNames = names;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
        }
    }

    public void addPlayerName(String name) {
        usedPlayerNames.add(name);
    }

    public void addGuildName(String name) {
        usedGuildNames.add(name);
    }

    public Set<String> getUsedPlayerNames() {
        return usedPlayerNames;
    }

    public Set<String> getUsedGuildNames() {
        return usedGuildNames;
    }

    public String getNextNameSuff() {
        return String.valueOf(nameIdFactory.getAndIncrement());
    }
}
