package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.mail.SimpleMail;

public class Test2 {

    public static void main(String[] args) {
        new Test2().run();
    }

    public void run() {
        ActorSystem actorSystem = new ActorSystem();
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

    class Player extends BaseActor {

        public Player(ActorSystem actorSystem) {
            super(actorSystem, "player");
        }
    }
}

