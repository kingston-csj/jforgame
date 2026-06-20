package jforgame.socket.core.dispatch;

import jforgame.socket.core.protocol.annotation.RequestHandler;
import jforgame.socket.core.session.IdSession;

/**
 * Message parameter converter, converts message parameters to actual method arguments annotated with {@link RequestHandler}
 */
public interface MessageParameterConverter {

     /**
      * Converts various parameters to actual method arguments annotated with {@link RequestHandler}
      *
      * @param session      socket session
      * @param methodParams method parameters
      * @param context      request context
      * @return converted method arguments
      */
     Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, RequestContext context);

}
