package jforgame.socket.mina;

import org.apache.mina.core.session.AttributeKey;

/**
 * session property enum
 * @author kinson
 */
public interface MinaSessionProperties {

	/** 洪水检查记录 */
	AttributeKey FLOOD = new AttributeKey(MinaSessionProperties.class, "FLOOD");
	/** 业务session */
	AttributeKey UserSession = new AttributeKey(MinaSessionProperties.class, "GameSession");
}
