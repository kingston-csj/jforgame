package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.config.ActorConfigLoader;
import jforgame.threadmodel.actor.config.ActorSystemConfig;

public class ConfigFromFileTest {

    public static void main(String[] args) throws InterruptedException {
        new ConfigFromFileTest().run();
    }

    public void run() throws InterruptedException {
        ActorSystemConfig config = ActorConfigLoader.loadFromClasspath("akka-actor.properties");
        
        ConfigurableActorSystem actorSystem = new ConfigurableActorSystem(config);
        
        System.out.println("=== Actor System Configuration ===");
        System.out.println("Default Dispatcher Core Pool Size: " + config.getDefaultDispatcher().getCorePoolSize());
        System.out.println("Default Dispatcher Max Pool Size: " + config.getDefaultDispatcher().getMaxPoolSize());
        System.out.println("Default Mailbox Type: " + config.getDefaultMailbox().getMailboxType());
        System.out.println("Default Mailbox Capacity: " + config.getDefaultMailbox().getMailboxCapacity());
        System.out.println("Bounded Mailbox Type: " + config.getMailboxConfig("bounded-mailbox").getMailboxType());
        System.out.println("Bounded Mailbox Capacity: " + config.getMailboxConfig("bounded-mailbox").getMailboxCapacity());
        System.out.println("Priority Mailbox Type: " + config.getMailboxConfig("priority-mailbox").getMailboxType());
        System.out.println("Priority Mailbox Capacity: " + config.getMailboxConfig("priority-mailbox").getMailboxCapacity());
        System.out.println("====================================");
        
        testDifferentActorTypes(actorSystem);
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Actor System Statistics ===");
        System.out.println(actorSystem.toString());
        
        actorSystem.shutDown();
    }

    private void testDifferentActorTypes(ConfigurableActorSystem actorSystem) {
        Actor playerActor = actorSystem.createActor("/player/player-123");
        Actor systemActor = actorSystem.createActor("/system/monitor");
        Actor priorityActor = actorSystem.createActor("/priority/emergency");
        
        playerActor.tell(new SimpleMail("login", "user123") {
            @Override
            public void action() {
                System.out.println("Player login: " + getContent()[0] + " (using bounded mailbox)");
            }
        });
        
        systemActor.tell(new SimpleMail("monitor", "health-check") {
            @Override
            public void action() {
                System.out.println("System monitor: " + getContent()[0] + " (using default mailbox)");
            }
        });
        
        priorityActor.tell(new SimpleMail("emergency", "critical-alert") {
            @Override
            public void action() {
                System.out.println("Priority emergency: " + getContent()[0] + " (using priority mailbox)");
            }
        });
        
        Actor existingPlayer = actorSystem.getActor("/player/player-123");
        if (existingPlayer != null) {
            existingPlayer.tell(new SimpleMail("action", "move") {
                @Override
                public void action() {
                    System.out.println("Player action: " + getContent()[0]);
                }
            });
        }
        
        Actor autoCreatedActor = actorSystem.getOrCreateActor("/custom/auto-actor");
        autoCreatedActor.tell(new SimpleMail("test", "auto-created") {
            @Override
            public void action() {
                System.out.println("Auto-created actor: " + getContent()[0] + " (using default config)");
            }
        });
    }
}
