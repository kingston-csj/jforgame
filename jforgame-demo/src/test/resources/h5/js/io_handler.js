/**
 * 与服务端的通信协议绑定
 */

var io_handler = io_handler || {}

io_handler.ReqAccountLogin = "101001";

io_handler.ResAccountLogin = "101051";

var self = io_handler;

var msgHandler = {}

io_handler.bind = function(msgId, handler) {
	msgHandler[msgId] = handler
}

self.bind(self.ResAccountLogin, function(resp) {
	alert("角色登录成功-->" + resp)
})

io_handler.handle = function(msgId, msg) {
	msgHandler[msgId](msg);
}
