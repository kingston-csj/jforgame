package jforgame.actor;

/**
 * actor基本任务
 * 根据消息类型进行分发
 */
public abstract class SimpleMail extends Mail {

    /**
     * 消息类型
     */
    private final String type;

    /**
     * 邮件内容， 由子类自行定义与解析
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
