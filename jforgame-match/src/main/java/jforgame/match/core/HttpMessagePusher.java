package jforgame.match.core;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpVersion;

import com.google.gson.Gson;
import jforgame.match.http.UrlResponse;
import jforgame.socket.message.Message;

public class HttpMessagePusher {

	public static void push(IoSession session, Message message) {
		if (session == null || message == null) {
			return;
		}
		String msgJson = new Gson().toJson(message);
		UrlResponse urlResponse = UrlResponse.valueOfSucc();
		urlResponse.setMessage(msgJson);
		urlResponse.setAttachment(message.getClass().getSimpleName());

		String responseHtml = new Gson().toJson(urlResponse);
		byte[] responseBytes= new byte[0];
		try {
			responseBytes = responseHtml.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int contentLength = responseBytes.length;

		// 构造HttpResponse对象，HttpResponse只包含响应的status line和header部分
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "text/html; charset=utf-8");
		headers.put("Content-Length", Integer.toString(contentLength));
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SUCCESS_OK, headers);

		// 响应BODY
		IoBuffer responseIoBuffer = IoBuffer.allocate(contentLength);
		responseIoBuffer.put(responseBytes);
		responseIoBuffer.flip();
		// 响应的status line和header部分
		session.write(response);
		// 响应body部分
		session.write(responseIoBuffer);
	}

}
