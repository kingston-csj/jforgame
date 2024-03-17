package jforgame.socket.share;

public interface MessageParameterConverter {

     /**
      * 将各种参数转为被RequestMapper注解的方法的实参
      *
      * @param session
      * @param methodParams
      * @param message
      * @return
      */
     Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, Object message);

}
