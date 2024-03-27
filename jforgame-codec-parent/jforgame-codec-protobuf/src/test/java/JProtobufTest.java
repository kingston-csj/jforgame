import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

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
            Assert.assertEquals(request2.getAccountId(), request.getAccountId());
            Assert.assertEquals(request2.getPassword(), request.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}