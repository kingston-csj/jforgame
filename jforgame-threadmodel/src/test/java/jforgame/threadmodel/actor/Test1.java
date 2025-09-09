package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.config.ActorSystemConfig;
import jforgame.threadmodel.actor.mail.SimpleMail;

public class Test1 {
    private static ActorSystemConfig actorSystemConfig = new ActorSystemConfig();

    public static void main(String[] args) {
        new Test1().run();
    }

    public void run() {
        ActorThreadModel actorSystem = new ActorThreadModel();
        Player player = new Player();
        player.actor = new AbsActor(actorSystem, "player", actorSystemConfig);

        for (int i = 0; i < 10; i++) {
            player.actor.tell(new SimpleMail("hello", i) {
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

    class Player {

        Actor actor;
    }
}

