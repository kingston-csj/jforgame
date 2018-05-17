package com.kingston.jforgame.server.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.kingston.jforgame.common.utils.FileUtils;
import com.kingston.jforgame.server.FireWallConfig;

public class JsConfigReaderTest {

	@Test
	public void testReadConfig() throws Exception {
		String content = FileUtils.readText("configs/firewall.cfg.js");
		
		FireWallConfig config = new FireWallConfig();
		Map<String, Object> params = new HashMap<>();
		params.put("config", config);
		
		String response = JsScriptEngine.runCode(content, params);
		System.err.println(config);
	}
	
}
