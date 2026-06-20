package jforgame.threadmodel.actor.config;

/**
 * Actor deployment configuration class
 */
public class ActorDeploymentConfig {

    /**
     * Mailbox name to use
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
