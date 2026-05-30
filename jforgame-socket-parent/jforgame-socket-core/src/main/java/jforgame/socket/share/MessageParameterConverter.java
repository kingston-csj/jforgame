package jforgame.socket.share;

/**
 * 消息参数转换器，用于将消息参数转换为被{@link jforgame.socket.share.annotation.RequestHandler}注解的方法的实参
 */
public interface MessageParameterConverter {

     /**
      * 将各种参数转为被{@link jforgame.socket.share.annotation.RequestHandler}注解的方法的实参
      *
      * @param session      会话
      * @param methodParams 方法参数
      * @param context      请求上下文
      * @return 转化后的方法实参
      */
     Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, RequestContext context);

}
