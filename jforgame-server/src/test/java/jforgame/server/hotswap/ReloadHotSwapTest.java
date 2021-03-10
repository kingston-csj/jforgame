package jforgame.server.hotswap;

import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;

public class ReloadHotSwapTest {

    @Test
    public void test() throws Exception {
        ServicePool.playerService.say("Hi");

        System.out.println("执行热更前，类加载器==" + ServicePool.playerService.getClass().getClassLoader());

        // 重新加载PlayerService class文件
        // 预先修改 say()方法后，把编译后的文件放到指定位置
        File f = new File("hotswap/PlayerService.class");
        byte[] targetClassFile = new byte[(int)f.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(f));
        dis.readFully(targetClassFile);
        dis.close();

        // 要用自定义类加载器，才能重新加载同名class文件
        DynamicClassLoader myLoader = new DynamicClassLoader();
        // 实例化新的对象
        Class<?> newClazz = myLoader.findClass(targetClassFile);
        System.out.println("执行热更后，类加载器==" + newClazz.getClassLoader());

        // 使用接口进行实例化
        IPlayerService newObj = (IPlayerService) newClazz.newInstance();
        // 反射注入
        Field field = ServicePool.class.getField("playerService");
        field.setAccessible(true);
        field.set(null, newObj);
        // PlayerService实例被替换
        ServicePool.playerService.say("Hi");
    }

}
