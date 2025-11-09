import jforgame.codec.MessageCodec;
import jforgame.codec.struct.StructMessageCodec;
import message.ItemVo;
import message.PlayerBackpack2;
import org.junit.Test;

public class TestMapMessage {

    private MessageCodec messageCodec = new StructMessageCodec();

    @Test
    public void test() throws Exception {
        PlayerBackpack2 backpack = new PlayerBackpack2();
        for (int i = 0; i < 10; i++) {
            ItemVo vo = new ItemVo();
            vo.setUid("uid_" + i);
            vo.setItemId(i);
            vo.setCount(i);
            backpack.getItems().put(vo.getUid(), vo);
        }

        byte[] encode = messageCodec.encode(backpack);

        PlayerBackpack2 newMsg = (PlayerBackpack2) messageCodec.decode(PlayerBackpack2.class, encode);
        System.out.println(newMsg);
    }
}
