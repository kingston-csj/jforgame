package com.kingston.jforgame.admin.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ResponseUtil {
	
	public static void responseJson(Map<String, Object> message, HttpServletResponse response) {
		try {
			response.setContentType("text/json;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.write(new Gson().toJson(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void responseJson(HttpResult message, HttpServletResponse response) {
		try {
			response.setContentType("text/json;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.write(new Gson().toJson(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
