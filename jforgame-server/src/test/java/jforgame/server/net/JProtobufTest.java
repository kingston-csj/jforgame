package jforgame.server.net;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import jforgame.server.game.login.message.req.ReqAccountLogin;

public class JProtobufTest {


	@Test
	public void testRequest() {
		ReqAccountLogin request = new ReqAccountLogin();
		request.setAccountId(123456L);
		request.setPassword("admin");
		Codec<ReqAccountLogin> simpleTypeCodec = ProtobufProxy
				.create(ReqAccountLogin.class);
		try {
			// 序列化
			byte[] bb = simpleTypeCodec.encode(request);
			// 反序列化
			ReqAccountLogin request2 = simpleTypeCodec.decode(bb);
			Assert.assertTrue(request2.getAccountId() == request.getAccountId());
			Assert.assertTrue(request2.getPassword().equals(request.getPassword()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
