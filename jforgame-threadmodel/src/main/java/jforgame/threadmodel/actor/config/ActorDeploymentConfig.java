package jforgame.threadmodel.actor.config;

/**
 * Actor部署配置类
 *
 * @author wupeng0528
 */
public class ActorDeploymentConfig {

    /**
     * 使用的邮箱名称
     */
    private String mailbox;

    public ActorDeploymentConfig() {
        this.mailbox = "default";
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }


    @Override
    public String toString() {
        return "ActorDeploymentConfig{" +
                ", mailbox='" + mailbox + '\'' +
                '}';
    }
}
