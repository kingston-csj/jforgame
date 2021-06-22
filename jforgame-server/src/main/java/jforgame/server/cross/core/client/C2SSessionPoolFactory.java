package jforgame.server.cross.core.client;

import jforgame.common.utils.NumberUtil;
import jforgame.server.ServerConfig;
import jforgame.server.cross.core.server.BaseCrossMessageDispatcher;
import jforgame.server.cross.core.server.CMessageDispatcher;
import jforgame.server.game.database.config.ConfigDataPool;
import jforgame.server.game.database.config.bean.ConfigCross;
import jforgame.server.game.database.config.storage.ConfigCrossStorage;
import jforgame.server.logs.LoggerUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * session对象池管理，borrow跟return的所有方法都改为 friendly权限（包内可见）
 * 原因：业务代码借东西不还就爆炸了。。。
 */
public class C2SSessionPoolFactory {

    private GenericObjectPoolConfig config;

    private ConcurrentMap<String, GenericObjectPool<CCSession>> pools = new ConcurrentHashMap<>();

    private static volatile C2SSessionPoolFactory self;

    public static C2SSessionPoolFactory getInstance() {
        if (self != null) {
            return self;
        }
        synchronized (C2SSessionPoolFactory.class) {
            if (self == null) {
                GenericObjectPoolConfig config = new GenericObjectPoolConfig();
                config.setMaxTotal(Runtime.getRuntime().availableProcessors());
                config.setTestOnBorrow(true);
                C2SSessionPoolFactory instance = new C2SSessionPoolFactory(config);
                self = instance;
            }
        }
        return self;
    }

    public C2SSessionPoolFactory(GenericObjectPoolConfig config) {
        this.config = config;
    }

    /**
     * 借东西记得换啊！！！
     * 有借有还，再借不难
     *
     * @param ip
     * @param port
     * @return
     */
    CCSession borrowSession(String ip, int port) {
        String key = buildKey(ip, port);
        try {
            C2SSessionFactory factory = new C2SSessionFactory(ip, port);
            GenericObjectPool<CCSession> pool = pools.getOrDefault(key, new GenericObjectPool(factory, config));
            pools.putIfAbsent(key, pool);
            return pool.borrowObject();
        } catch (Exception e) {
            LoggerUtils.error("", e);
            return null;
        }
    }

    /**
     * 获取本游戏服分配的大区战斗服
     *
     * @return
     */
    CCSession borrowCrossSession() {
        ConfigCrossStorage storage = ConfigDataPool.getInstance().getStorage(ConfigCrossStorage.class);
        ServerConfig serverConfig = ServerConfig.getInstance();
        // 先拿到本服对应的跨服服务器id
        ConfigCross selfServerCross = storage.getConfigCrossBy(serverConfig.getServerId());
        int crossSeverId = selfServerCross.getCrossServer();
        // 再拿到跨服服务器的ip和端口
        ConfigCross targetServerCross = storage.getConfigCrossBy(crossSeverId);
        String ip = targetServerCross.getIp();
        int port = targetServerCross.getRpcPort();
        return borrowSession(ip, port);
    }

    /**
     * 获取匹配服链接
     *
     * @return
     */
    CCSession borrowCenterSession() {
        String matchUrl = ServerConfig.getInstance().getMatchUrl();
        String ip = matchUrl.split(":")[0];
        int port = NumberUtil.intValue(matchUrl.split(":")[1]);
        return borrowSession(ip, port);
    }

    void returnSession(CCSession session) {
        String key = buildKey(session.getIpAddr(), session.getPort());
        GenericObjectPool<CCSession> pool = pools.get(key);
        if (pool != null) {
            pool.returnObject(session);
        }
    }

    private String buildKey(String ip, int port) {
        return ip + "-" + port;
    }
}

class C2SSessionFactory extends BasePooledObjectFactory<CCSession> {

    String ip;

    int port;

    CMessageDispatcher dispatcher;

    public C2SSessionFactory(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
        this.dispatcher = BaseCrossMessageDispatcher.getInstance();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public CCSession create() throws Exception {
        CCSession session = CCSession.valueOf(ip, port, dispatcher);
        session.buildConnection();
        return session;
    }

    @Override
    public boolean validateObject(PooledObject<CCSession> p) {
        return !p.getObject().isExpired();
    }

    @Override
    public PooledObject<CCSession> wrap(CCSession obj) {
        return new DefaultPooledObject<CCSession>(obj);
    }

}
