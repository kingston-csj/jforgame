package com.kingston.test.net;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.kingston.game.login.message.ReqLoginMessage;

public class TestJProtobuf {


	@Test
	public void testRequest() {
		ReqLoginMessage request = new ReqLoginMessage();
		request.setPlayerId(123456L);
		request.setPassword("kingston");
		Codec<ReqLoginMessage> simpleTypeCodec = ProtobufProxy
				.create(ReqLoginMessage.class);
		try {
			// 序列化
			byte[] bb = simpleTypeCodec.encode(request);
			// 反序列化
			ReqLoginMessage request2 = simpleTypeCodec.decode(bb);
			Assert.assertTrue(request2.getPlayerId() == request.getPlayerId());
			Assert.assertTrue(request2.getPassword().equals(request.getPassword()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
