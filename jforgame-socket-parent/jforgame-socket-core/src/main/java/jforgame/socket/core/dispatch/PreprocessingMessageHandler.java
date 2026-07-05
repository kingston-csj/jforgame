package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.registry.MessageHandlerRegister;
import jforgame.socket.core.registry.MessageExecutor;
import jforgame.socket.core.protocol.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preprocesses messages sent by clients.
 * Includes: binding message handlers, converting parameters to handler method arguments.
 * @since 3.0.0
 */
public class PreprocessingMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(PreprocessingMessageHandler.class);

    /**
     * Message factory
     */
    private MessageFactory messageFactory;
    /**
     * Parameter converter
     */
    private MessageParameterConverter messageParameterConverter;

    /**
     * Message handler register
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
