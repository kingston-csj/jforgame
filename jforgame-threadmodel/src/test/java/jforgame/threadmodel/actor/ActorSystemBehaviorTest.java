package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.config.ActorDeploymentConfig;
import jforgame.threadmodel.actor.config.ActorSystemConfig;
import jforgame.threadmodel.actor.config.MailboxConfig;
import jforgame.threadmodel.actor.mail.PriorityMail;
import jforgame.threadmodel.actor.mail.SimpleMail;
import jforgame.threadmodel.actor.mailbox.PriorityMailbox;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ActorSystemBehaviorTest {

    @Test
    public void removeActorShouldDeactivateExistingReference() throws Exception {
        ActorSystem actorSystem = new ActorSystem();
        try {
            Actor actor = actorSystem.createActor("/player/remove-test");
            actorSystem.removeActor("/player/remove-test");

            CountDownLatch latch = new CountDownLatch(1);
            actor.tell(new SimpleMail("after-remove") {
                @Override
                public void action() {
                    latch.countDown();
                }
            });

            Assert.assertNull(actorSystem.getActor("/player/remove-test"));
            Assert.assertFalse(latch.await(200, TimeUnit.MILLISECONDS));
        } finally {
            actorSystem.shutDown();
        }
    }

    @Test
    public void acceptAfterShutdownShouldThrowClearException() {
        ActorSystem actorSystem = new ActorSystem();
        actorSystem.shutDown();

        try {
            actorSystem.accept(() -> {
            });
            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("shutdown"));
        }
    }

    @Test
    public void priorityMailboxShouldPreferPriorityMailOverNormalMail() {
        MailboxConfig config = new MailboxConfig();
        config.setType(MailboxConfig.TYPE_PRIORITY);
        PriorityMailbox mailbox = new PriorityMailbox(config);

        SimpleMail normalMail = new SimpleMail("normal") {
            @Override
            public void action() {
            }
        };
        PriorityMail highPriorityMail = new PriorityMail("high", PriorityMail.HIGH_PRIORITY) {
            @Override
            public void action() {
            }
        };

        mailbox.receive(normalMail);
        mailbox.receive(highPriorityMail);

        Assert.assertSame(highPriorityMail, mailbox.poll());
        Assert.assertSame(normalMail, mailbox.poll());
    }

    @Test
    public void deploymentConfigShouldPreferLongestMatchingPattern() {
        ActorSystemConfig config = new ActorSystemConfig();

        ActorDeploymentConfig commonPlayerConfig = new ActorDeploymentConfig();
        commonPlayerConfig.setMailbox(ActorSystemConfig.MAILBOX_BOUNDED);
        config.registerDeploymentConfig("/player/*", commonPlayerConfig);

        ActorDeploymentConfig vipPlayerConfig = new ActorDeploymentConfig();
        vipPlayerConfig.setMailbox(ActorSystemConfig.MAILBOX_PRIORITY);
        config.registerDeploymentConfig("/player/vip/*", vipPlayerConfig);

        ActorDeploymentConfig matched = config.getDeploymentConfig("/player/vip/1001");
        Assert.assertSame(vipPlayerConfig, matched);
    }
}
