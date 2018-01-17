var o = {};
o.age = 12;
o.name = "Tom";
o.sayHello = function() {
    print("Hi");
}


com.kingston.jforgame.server.logs.LoggerUtils.error("执行js测试脚本");

com.kingston.jforgame.server.logs.LoggerUtils.error("调用js对象方法-->");
o.sayHello();


