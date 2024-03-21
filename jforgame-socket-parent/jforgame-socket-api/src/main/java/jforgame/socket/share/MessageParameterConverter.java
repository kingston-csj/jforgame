package jforgame.socket.share;

import java.lang.reflect.Method;

public interface MessageParameterConverter {

     /**
      * 将各种参数转为被RequestMapper注解的方法的实参
      *
      * @param session socket session
      * @param methodParams params of the message
      * @param message request message
      * @return object array for method invoke  {@link Method#invoke}
      */
     Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, Object message);

}
