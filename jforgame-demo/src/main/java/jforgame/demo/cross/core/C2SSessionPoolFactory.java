package jforgame.demo.cross.core;

import io.netty.channel.Channel;
import jforgame.codec.struct.StructMessageCodec;
import jforgame.commons.NumberUtil;
import jforgame.demo.ServerConfig;
import jforgame.demo.ServerScanPaths;
import jforgame.demo.game.database.config.ConfigDataPool;
import jforgame.demo.game.database.config.bean.ConfigCross;
import jforgame.demo.game.database.config.storage.ConfigCrossStorage;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.MessageIoDispatcher;
import jforgame.socket.client.SocketClient;
import jforgame.socket.netty.support.client.NSocketClient;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * session对象池管理，borrow跟return的所有方法都改为 friendly权限（包内可见）
 * 原因：业务代码借东西不还就爆炸了。。。
 */
public class C2SSessionPoolFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GenericObjectPoolConfig config;

    private ConcurrentMap<String, GenericObjectPool<NSessionPlus>> pools = new ConcurrentHashMap<>();

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
                self = new C2SSessionPoolFactory(config);
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
    public NSessionPlus borrowSession(String ip, int port) {
        String key = buildKey(ip, port);
        try {
            C2SSessionFactory factory = new C2SSessionFactory(ip, port);
            GenericObjectPool<NSessionPlus> pool = pools.getOrDefault(key, new GenericObjectPool<>(factory, config));
            pools.putIfAbsent(key, pool);
            return pool.borrowObject();
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    /**
     * 获取本游戏服分配的大区战斗服
     *
     * @return
     */
    NSessionPlus borrowCrossSession() {
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
    NSessionPlus borrowCenterSession() {
        String matchUrl = ServerConfig.getInstance().getMatchUrl();
        String ip = matchUrl.split(":")[0];
        int port = NumberUtil.intValue(matchUrl.split(":")[1]);
        return borrowSession(ip, port);
    }

    void returnSession(NSessionPlus session) {
        String key = buildKey(session.getLocalIP(), session.getLocalPort());
        GenericObjectPool<NSessionPlus> pool = pools.get(key);
        if (pool != null) {
            pool.returnObject(session);
        }
    }

    private String buildKey(String ip, int port) {
        return ip + "-" + port;
    }
}

class C2SSessionFactory extends BasePooledObjectFactory<NSessionPlus> {

    String ip;

    int port;

    public C2SSessionFactory(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
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
    public NSessionPlus create() throws Exception {
        SocketClient clientFactory = new NSocketClient(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH), GameMessageFactory.getInstance(), new StructMessageCodec(), HostAndPort.valueOf(ip, port));
        IdSession session = clientFactory.openSession();
        return new NSessionPlus((Channel) session.getRawSession());
    }

    @Override
    public boolean validateObject(PooledObject<NSessionPlus> p) {
        return !p.getObject().isExpired();
    }

    @Override
    public PooledObject<NSessionPlus> wrap(NSessionPlus obj) {
        return new DefaultPooledObject<NSessionPlus>(obj);
    }

}
