package jforgame.actor;

import java.util.LinkedList;
import java.util.List;

public class Test3 {

    public static void main(String[] args) {
        new Test3().run();
    }

    public void run() {
        ActorSystem actorSystem = new ActorSystem();
        Player player = new Player(actorSystem, 100, 20, "孙悟空");
        Player player2 = new Player(actorSystem, 150, 15, "猪八戒");
        Monster monster = new Monster(actorSystem, 300, 25, "牛魔大王");

        List<Player> players = new LinkedList<>();
        players.add(player);
        players.add(player2);
        List<Monster> monsters = new LinkedList<>();
        monsters.add(monster);
        Scene scene = new Scene(actorSystem, players, monsters);
        while (!scene.isOver()) {
            for (Player p : players) {
                if (!p.isDead()) {
                    scene.tell(new Attack(p, monster));
                }

            }
            for (Monster m : monsters) {
                if (!m.isDead()) {
                    List<Player> tmpPlayers = new LinkedList<>();
                    if (!player.isDead()) {
                        tmpPlayers.add(player);
                    }
                    if (!player2.isDead()) {
                        tmpPlayers.add(player2);
                    }
                    // 随机杀一个
                    int index = (int) (Math.random() * tmpPlayers.size());
                    Player target = tmpPlayers.get(index);
                    scene.tell(new Attack(m, target));
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        actorSystem.shutdown();
    }

    class Creature {
        int hp;

        int atk;

        String name;

        public Creature(int hp, int atk, String name) {
            this.hp = hp;
            this.atk = atk;
            this.name = name;
        }

        public boolean isDead() {
            return hp <= 0;
        }
    }

    class Player extends Creature {

        Actor actor;

        public Player(ActorSystem actorSystem, int hp, int atk, String name) {
            super(hp, atk, name);
            this.actor = new AbsActor(actorSystem);
        }

    }

    class Monster extends Creature {
        Actor actor;

        public Monster(ActorSystem actorSystem, int hp, int atk, String name) {
            super(hp, atk, name);
            this.actor = new AbsActor(actorSystem);
        }
    }

    /**
     * 战斗场景
     */
    class Scene extends AbsActor {

        private List<Player> players;

        private List<Monster> monsters;

        public Scene(ActorSystem actorSystem, List<Player> players, List<Monster> monsters) {
            super(actorSystem);
            this.players = players;
            this.monsters = monsters;
        }

        public boolean isOver() {
            // player全死或者monster全死
            return players.stream().allMatch(p -> p.hp <= 0) || monsters.stream().allMatch(m -> m.hp <= 0);
        }
    }

    class Attack extends SimpleMail {
        private Creature attacker;

        private Creature target;

        public Attack(Creature attacker, Creature target) {
            super("attack");
            this.attacker = attacker;
            this.target = target;
        }

        @Override
        public void run() {
            if (target.isDead()) {
                return;
            }
            if (attacker.isDead()) {
                return;
            }
            int damage = (int) (Math.random() * attacker.atk) + 1;
            target.hp -= damage;
            if (target.hp <= 0) {
                System.out.println(attacker.name + " attack " + target.name + ",  " + target.name + "is dead");
            } else {
                System.out.println(attacker.name + " attack " + target.name + ",  " + target.name + " hp is " + target.hp);
            }
        }
    }
}

