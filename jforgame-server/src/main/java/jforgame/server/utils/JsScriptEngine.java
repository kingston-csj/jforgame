package jforgame.server.utils;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

public class JsScriptEngine {

	public static String runCode(String jsCode, Map<String, Object> params) {
		try {
			ScriptEngineManager engineManager= new ScriptEngineManager();
			ScriptEngine scriptEngine = engineManager.getEngineByName("JavaScript");
			return scriptEngine.eval(jsCode, new SimpleBindings(params)).toString();
		} catch (Exception e) {
			throw new RuntimeException("执行js脚本出错,"+jsCode);
		}
	}
	
	public static String runCode(String jsCode) {
		return runCode(jsCode, new HashMap<>());
	}
	
}
