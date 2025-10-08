package jforgame.socket.share;

import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.support.DefaultMessageParameterConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对客户端发送的消息进行预处理
 * 包括：绑定消息处理器， 将相关参数转化为处理器的方法入参
 */
public class PreprocessingMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    /**
     * 消息工厂
     */
    private MessageFactory messageFactory;
    /**
     * 参数转换器
     */
    private MessageParameterConverter messageParameterConverter;

    /**
     * 消息处理注册器
     */
    private MessageHandlerRegister messageHandlerRegister;

    public PreprocessingMessageHandler(MessageFactory messageFactory, MessageParameterConverter msgParameterConverter, MessageHandlerRegister messageHandlerRegister) {
        this.messageFactory = messageFactory;
        this.messageParameterConverter = msgParameterConverter;
        this.messageHandlerRegister = messageHandlerRegister;
    }

    public PreprocessingMessageHandler(MessageFactory messageFactory, MessageHandlerRegister messageHandlerRegister) {
        this.messageFactory = messageFactory;
        this.messageParameterConverter = new DefaultMessageParameterConverter(messageFactory);
        this.messageHandlerRegister = messageHandlerRegister;
    }


    @Override
    public boolean messageReceived(IdSession session, RequestContext context) throws Exception {
        int cmd = messageFactory.getMessageId(context.getRequest().getClass());
        MessageExecutor cmdExecutor = messageHandlerRegister.getMessageExecutor(cmd);
        if (cmdExecutor == null) {
            logger.error("message executor missed,  cmd={}", cmd);
            return false;
        }
        context.setMethodExecutor(cmdExecutor);
        Object[] params = messageParameterConverter.convertToMethodParams(session, cmdExecutor.getParams(), context);
        context.setParams(params);
        return true;
    }

}
