package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.config.ActorSystemConfig;
import jforgame.threadmodel.actor.config.MailboxConfig;
import jforgame.threadmodel.actor.mail.PriorityMail;
import jforgame.threadmodel.actor.mail.SimpleMail;

public class ConfigurableActorTest {

    public static void main(String[] args) throws InterruptedException {
        new ConfigurableActorTest().run();
    }

    public void run() throws InterruptedException {
        ActorSystemConfig config = createCustomConfig();

        ActorSystem actorSystem = new ActorSystem(config);

        // 测试不同类型的Actor
        testPlayerActor(actorSystem);
        testPriorityActor(actorSystem);
        testSystemActor(actorSystem);

        Thread.sleep(2000);

        System.out.println(actorSystem.toString());

        actorSystem.shutDown();
    }

    private ActorSystemConfig createCustomConfig() {
        ActorSystemConfig config = new ActorSystemConfig();

        MailboxConfig customBoundedMailbox = new MailboxConfig();
        customBoundedMailbox.setType(MailboxConfig.TYPE_BOUNDED);
        customBoundedMailbox.setCapacity(100);
        config.registerMailboxConfig("custom-bounded-mailbox", customBoundedMailbox);

        return config;
    }

    private void testPlayerActor(ActorSystem actorSystem) {
        Actor playerActor = actorSystem.createActor("/player/player-001");

        for (int i = 0; i < 20; i++) {
            final int msgId = i;
            playerActor.tell(new SimpleMail("player-action", msgId) {
                @Override
                public void action() {
                    System.out.println("Player action: " + getContent()[0]);
                    try {
                        Thread.sleep(50); // 模拟处理时间
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
    }

    private void testPriorityActor(ActorSystem actorSystem) {
        Actor priorityActor = actorSystem.createActor("/priority/urgent-handler");

        priorityActor.tell(new PriorityMail("normal-task", PriorityMail.NORMAL_PRIORITY, "Normal priority task") {
            @Override
            public void action() {
                System.out.println("Processing: " + getContent()[0] + " (Priority: " + getPriority() + ")");
            }
        });

        priorityActor.tell(new PriorityMail("high-task", PriorityMail.HIGH_PRIORITY, "High priority task") {
            @Override
            public void action() {
                System.out.println("Processing: " + getContent()[0] + " (Priority: " + getPriority() + ")");
            }
        });

        priorityActor.tell(new PriorityMail("low-task", PriorityMail.LOW_PRIORITY, "Low priority task") {
            @Override
            public void action() {
                System.out.println("Processing: " + getContent()[0] + " (Priority: " + getPriority() + ")");
            }
        });
    }

    private void testSystemActor(ActorSystem actorSystem) {
        Actor systemActor = actorSystem.createActor("/system/logger");

        for (int i = 0; i < 5; i++) {
            final String logMsg = "System log " + i;
            systemActor.tell(new SimpleMail("log", logMsg) {
                @Override
                public void action() {
                    System.out.println("System Log: " + getContent()[0]);
                }
            });
        }
    }
}
