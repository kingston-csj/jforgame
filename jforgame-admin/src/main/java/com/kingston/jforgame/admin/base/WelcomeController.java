package com.kingston.jforgame.admin.base;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WelcomeController {

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		return "welcome";
	}

	@RequestMapping(value = "hello")
	@ResponseBody
	public String hello() {

		return "hello spring boot";

	}

}
