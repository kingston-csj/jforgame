package jforgame.server.cross.core.client;

import jforgame.common.thread.NamedThreadFactory;
import jforgame.server.cross.core.callback.CallBackService;
import jforgame.server.cross.core.callback.CallTimeoutException;
import jforgame.server.cross.core.callback.G2FCallBack;
import jforgame.server.cross.core.callback.RequestCallback;
import jforgame.server.cross.core.callback.RequestResponseFuture;
import jforgame.socket.HostAndPort;
import jforgame.socket.message.Message;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CrossTransportManager {

    private static volatile CrossTransportManager instance;

    private int defaultCoreSum = Runtime.getRuntime().availableProcessors();

    private ExecutorService[] services;

    private C2SSessionPoolFactory sessionFactory;

    private AtomicInteger idFactory = new AtomicInteger();

    public static CrossTransportManager getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (CrossTransportManager.class) {
            if (instance == null) {
                CrossTransportManager obj = new CrossTransportManager();
                obj.init();
                instance = obj;
            }

        }
        return instance;
    }

    private void init() {
        services = new ExecutorService[defaultCoreSum];
        for (int i = 0; i < defaultCoreSum; i++) {
            services[i] = Executors.newSingleThreadExecutor(new NamedThreadFactory("cross-ladder-transport" + i));
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(5);
        config.setMaxWaitMillis(5000);
        sessionFactory = new C2SSessionPoolFactory(config);
    }

    /**
     * 同步发消息
     *
     * @param ip
     * @param port
     * @param message
     */
    public void sendMessage(String ip, int port, Message message) {
        CCSession session = sessionFactory.borrowSession(ip, port);
        session.sendMessage(message);
    }

    /**
     * 异步发消息
     *
     * @param ip
     * @param port
     * @param message
     */
    public void sendMessageAsync(String ip, int port, Message message) {
        String key = ip + port;
        int index = key.hashCode() % defaultCoreSum;
        services[index].submit(() -> {
            sendMessage(ip, port, message);
        });
    }

    /**
     * 发送消息并返回执行结果(类似rpc消息返回值)
     *
     * @param addr
     * @param request
     * @return
     */
    public Message request(HostAndPort addr, G2FCallBack request) throws InterruptedException, CallTimeoutException {
        int timeout = 5000;
        int index = request.getIndex();
        request.serialize();
        CCSession session = C2SSessionPoolFactory.getInstance().borrowSession(addr.getHost(), addr.getPort());
        session.sendMessage(request);

        final RequestResponseFuture future = new RequestResponseFuture(index,  timeout,null);
        try {
            CallBackService.getInstance().register(index, future);
            Message responseMessage = future.waitResponseMessage(timeout);
            if (responseMessage == null) {
                CallTimeoutException exception = new CallTimeoutException("send request message  failed");
                future.setCause(exception);
                throw exception;
            } else {
                return responseMessage;
            }
        } catch (InterruptedException e) {
            future.setCause(e);
            throw e;
        } finally {
            CallBackService.getInstance().remove(index);
            C2SSessionPoolFactory.getInstance().returnSession(session);
        }
    }


    /**
     * 发送消息并注册回调任务
     *
     * @param addr
     * @param request
     * @return
     */
    public void request(HostAndPort addr, G2FCallBack request, RequestCallback callBack) {
        CCSession session = C2SSessionPoolFactory.getInstance().borrowSession(addr.getHost(), addr.getPort());
        request.serialize();
        int timeout = 5000;
        int index = request.getIndex();
        final RequestResponseFuture requestResponseFuture = new RequestResponseFuture(index, timeout, callBack);
        CallBackService.getInstance().register(index, requestResponseFuture);
        session.sendMessage(request, ()->{
            C2SSessionPoolFactory.getInstance().returnSession(session);
        });
    }

}
