package jforgame.actor;

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
                public void run() {
                    System.out.println(getType() + " " + getContent()[0]);
                }
            });
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        actorSystem.shutdown();
    }

    class Player extends AbsActor {

        public Player(ActorSystem actorSystem) {
            super(actorSystem);
        }
    }
}

