package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.config.ActorSystemConfig;
import jforgame.threadmodel.actor.mail.SimpleMail;

public class Test2 {
    private static ActorSystemConfig actorSystemConfig = new ActorSystemConfig();

    public static void main(String[] args) {
        new Test2().run();
    }

    public void run() {
        ActorThreadModel actorSystem = new ActorThreadModel();
        Player player = new Player(actorSystem);

        for (int i = 0; i < 10; i++) {
            player.tell(new SimpleMail("hello", i) {
                @Override
                public void action() {
                    System.out.println(getType() + " " + getContent()[0]);
                }
            });
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        actorSystem.shutDown();
    }

    class Player extends AbsActor {

        public Player(ActorThreadModel actorSystem) {
            super(actorSystem, "player", actorSystemConfig);
        }
    }
}

