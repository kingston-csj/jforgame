package jforgame.threadmodel.actor.mail;

/**
 * Actor basic task
 * Dispatches based on message type
 */
public abstract class SimpleMail extends Mail {

    /**
     * Message type
     */
    private final String type;

    /**
     * Mail content, defined and parsed by subclasses
     */
    private final Object[] content;

    public SimpleMail(String type, Object... content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public Object[] getContent() {
        return content;
    }

}
