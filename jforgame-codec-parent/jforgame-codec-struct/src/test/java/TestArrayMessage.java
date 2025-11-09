import jforgame.codec.MessageCodec;
import jforgame.codec.struct.StructMessageCodec;
import message.ItemVo;
import message.PlayerBackpack;
import org.junit.Test;

public class TestArrayMessage {

    private MessageCodec messageCodec = new StructMessageCodec();

    @Test
    public void test() throws Exception {
        PlayerBackpack backpack = new PlayerBackpack();
        for (int i = 0; i < 10; i++) {
            ItemVo vo = new ItemVo();
            vo.setItemId(i);
            vo.setCount(i);
            backpack.getItems().add(vo);
        }

        byte[] encode = messageCodec.encode(backpack);

        PlayerBackpack newMsg = (PlayerBackpack) messageCodec.decode(PlayerBackpack.class, encode);
        System.out.println(newMsg);
    }
}
