package jforgame.codec.struct;

import jforgame.codec.MessageCodec;
import jforgame.codec.struct.message.PlayerLevelUpMessage;
import org.junit.Assert;
import org.junit.Test;

public class TestChildMessage {

    private MessageCodec messageCodec = new StructMessageCodec();

    @Test
    public void test() throws Exception {

        PlayerLevelUpMessage childMsg = new PlayerLevelUpMessage();
        childMsg.setPlayerId(123456L);
        childMsg.setAfterLevel(100);

        byte[] encode = messageCodec.encode(childMsg);

        PlayerLevelUpMessage newMsg = (PlayerLevelUpMessage) messageCodec.decode(PlayerLevelUpMessage.class, encode);
        Assert.assertEquals(100, newMsg.getAfterLevel());
        Assert.assertEquals(123456L, newMsg.getPlayerId());
    }

}
