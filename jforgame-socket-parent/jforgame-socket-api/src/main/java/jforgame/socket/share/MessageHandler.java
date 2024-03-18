package jforgame.socket.share;

public interface MessageHandler {

    /**
     * a sorted list of MessageHandler generates a message handler chain,
     * when message handler {@link  jforgame.socket.share.SocketIoDispatcher#dispatch(IdSession, Object)} receives a message,
     * each of the chain will invoke this method
     * @param session target session
     * @param message the message received
     * @return true if you need the chain to pass the message to the next {@link MessageHandler} node
     */
    boolean messageReceived(IdSession session, Object message) throws Exception ;

}
