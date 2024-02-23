package jforgame.demo.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import jforgame.commons.FileUtils;
import jforgame.demo.FireWallConfig;

public class JsConfigReaderTest {

	@Test
	public void testReadConfig() throws Exception {
		String content = FileUtils.readLines("configs/firewall.cfg.js");
		
		FireWallConfig config = new FireWallConfig();
		Map<String, Object> params = new HashMap<>();
		params.put("config", config);
		
		String response = JsScriptEngine.runCode(content, params);
		System.err.println(config);
	}
	
}
