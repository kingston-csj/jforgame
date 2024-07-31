package jforgame.socket.mina.support.server;

import jforgame.codec.MessageCodec;
import jforgame.socket.mina.support.DefaultProtocolCodecFactory;
import jforgame.socket.mina.support.DefaultSocketIoHandler;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
import jforgame.socket.share.message.MessageFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TcpSocketServer implements ServerNode {

    private final Logger logger = LoggerFactory.getLogger(TcpSocketServer.class);

    private static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final Executor executor = Executors.newCachedThreadPool();

    private static final SimpleIoProcessorPool<NioSession> pool =
            new SimpleIoProcessorPool<>(NioProcessor.class, executor, CPU_CORE_SIZE);

    protected SocketAcceptor acceptor;

    protected List<HostAndPort> nodesConfig;

    protected MessageFactory messageFactory;

    protected MessageCodec messageCodec;

    protected ProtocolCodecFactory protocolCodecFactory;

    protected ChainedMessageDispatcher socketIoDispatcher;

    protected int maxProtocolSize;

    /**
     * start Mina socket server
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());

        acceptor = new NioSocketAcceptor(pool);
        acceptor.setReuseAddress(true);
        acceptor.getSessionConfig().setAll(new DefaultSocketSessionConfig());

        DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();

        if (protocolCodecFactory != null) {
            filterChain.addLast("codec",
                    new ProtocolCodecFilter(protocolCodecFactory));
        } else {
            filterChain.addLast("codec",
                    new ProtocolCodecFilter(new DefaultProtocolCodecFactory(messageFactory, messageCodec, maxProtocolSize)));
        }

        //指定业务逻辑处理器
        acceptor.setHandler(new DefaultSocketIoHandler(socketIoDispatcher));

        for (HostAndPort node : nodesConfig) {
            logger.info("socket server is listening at " + node.getPort() + "......");
            acceptor.bind(new InetSocketAddress(node.getPort()));
        }
    }

    @Override
    public void shutdown() throws Exception {
        if (acceptor != null) {
            acceptor.unbind();
            acceptor.dispose();
        }
        logger.error("---------> socket server stop successfully");
    }

}