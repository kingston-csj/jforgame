package jforgame.demo.hotswap;

import jforgame.commons.util.FileUtil;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;

public class ReloadHotSwapTest2 {

    @Test
    public void test() throws Exception {
        ServicePool.playerService.say("Hi");

        System.out.println("执行热更前，类加载器==" + ServicePool.playerService.getClass().getClassLoader());
        try {
            String filePath = "script" + File.separator + "script.groovy";
            String groovyCode = FileUtil.readFullText(filePath);

            ScriptEngineManager engineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = engineManager.getEngineByName("groovy");
            scriptEngine.eval(groovyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("执行热更后，类加载器==" + ServicePool.playerService.getClass().getClassLoader());
        // PlayerService实例被替换
        ServicePool.playerService.say("Hi");
    }

}
