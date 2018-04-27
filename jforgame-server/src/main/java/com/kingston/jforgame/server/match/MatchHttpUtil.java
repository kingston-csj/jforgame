package com.kingston.jforgame.server.match;

import java.io.IOException;

import com.google.gson.Gson;
import com.kingston.jforgame.common.utils.HttpUtil;
import com.kingston.jforgame.net.socket.message.Message;
import com.kingston.jforgame.server.ServerConfig;

public class MatchHttpUtil {

	public static Message submit(AbstractMatchMessage request) throws IOException {
		String signature = request.getClass().getSimpleName();
		String data = new Gson().toJson(request);
		String param = HttpUtil.buildUrlParam("service", signature,
				"param", data);


		String url = ServerConfig.getInstance().getMatchUrl() + "?" + param;
		System.err.println("发送url>>>>>>" + url);
		String resultJson = HttpUtil.get(url);
		UrlResponse urlResponse = new Gson().fromJson(resultJson, UrlResponse.class);

		String respClazz = urlResponse.getAttachemt();
		Class<?> msgClazz = MatchMessageFactory.getInstance().getMessageBy(respClazz);
		Message msgResponse = (Message)new Gson().fromJson(urlResponse.getMessage(), msgClazz);
		return msgResponse;
	}

}
