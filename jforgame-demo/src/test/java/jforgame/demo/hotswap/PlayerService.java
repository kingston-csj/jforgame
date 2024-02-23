package jforgame.demo.hotswap;

public class PlayerService implements IPlayerService{

    public void say(String word) {
        System.out.println("before hotSwap, say " + word);
    }

}
