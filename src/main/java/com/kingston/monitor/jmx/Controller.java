package com.kingston.monitor.jmx;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.kingston.game.player.PlayerManager;

public class Controller implements ControllerMBean{

	@Override  
	public int getOnlinePlayerSum() {  
		return PlayerManager.getInstance().getOnlinePlayers().size();  
	}  


	@Override  
	public String getMemoryInfo() {  
		StringBuilder sb = new StringBuilder();  
		sb.append("free:")  
		.append(Runtime.getRuntime().freeMemory())  
		.append(",\n")  
		.append("total:")  
		.append(Runtime.getRuntime().totalMemory());  

		return sb.toString();  
	}  
	
	@Override
	public String execJavascript(String jsCode){
		
        String msg = "执行成功";
		try {
			ScriptEngineManager engineManager= new ScriptEngineManager();
			ScriptEngine scriptEngine = engineManager.getEngineByName("JavaScript");
			return scriptEngine.eval(jsCode).toString();
		} catch (Exception e) {
            msg = e.getMessage();
		}
		return msg;
	}


}
