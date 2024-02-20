package jforgame.socket.client;

public interface Traceable {

    /**
     * get index of the message
     * @return
     */
    int getIndex();

    /**
     * set index of the message
     * this method will be invoked automaticly
     * @return
     */
    void setIndex(int index);
}
