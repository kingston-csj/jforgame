
// 查看内存数据
var playerSize = com.kinson.jforgame.server.game.player.PlayerManager.getInstance().getOnlinePlayers();

com.kinson.jforgame.server.logs.LoggerUtils.error("在线玩家数据为" + playerSize);

// 调用java代码
com.kinson.jforgame.server.game.player.PlayerManager.getInstance().kickPlayer(123);

com.kinson.jforgame.server.logs.LoggerUtils.error("调用js对象方法-->");
var o = {};
o.age = 12;
o.name = "Tom";
o.sayHello = function() {
    print("Hi");
}
o.sayHello();


