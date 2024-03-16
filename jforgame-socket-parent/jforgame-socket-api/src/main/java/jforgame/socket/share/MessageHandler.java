package jforgame.socket.share;

public interface MessageHandler {

    /**
     * a sorted list of MessageHandler generates a message handler chain,
     * when message handler {@link  jforgame.socket.share.SocketIoDispatcher#dispatch(IdSession, Object)} receives a message,
     * each of the chain will invoke this method
     * @param session
     * @param message
     * @return true if chain passes the message to the next MessageHandler node
     */
    boolean messageReceived(IdSession session, Object message) throws Exception ;

}
