package jforgame.threadmodel.actor;

public class Test1 {

    public static void main(String[] args) {
        new Test1().run();
    }

    public void run() {
        ActorThreadModel actorSystem = new ActorThreadModel();
        Player player = new Player();
        player.actor = new AbsActor(actorSystem);

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

