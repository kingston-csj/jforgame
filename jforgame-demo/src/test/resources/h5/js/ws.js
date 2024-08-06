/**
 * 对webSocket的封装
 */
(function ($) {
  $.config = {
    url: "", //链接地址
  };

  $.init = function (config) {
    this.config = config;
    return this;
  };

  /**
   * 连接webcocket
   */
  $.connect = function () {
    var protocol = "ws:";
    this.host = protocol + this.config.url;

    window.WebSocket = window.WebSocket || window.MozWebSocket;
    if (!window.WebSocket) {
      // 检测浏览器支持
      this.error("Error: WebSocket is not supported .");
      return;
    }
    this.socket = new WebSocket(this.host); // 创建连接并注册响应函数
    this.socket.onopen = function () {
      $.onopen();
    };
    this.socket.onmessage = function (message) {
      $.onmessage(message);
    };
    this.socket.onclose = function () {
      $.onclose();
      $.socket = null; // 清理
    };
    this.socket.onerror = function (errorMsg) {
      $.onerror(errorMsg);
    };
    return this;
  };

  /**
   * 自定义异常函数
   * @param {Object} errorMsg
   */
  $.error = function (errorMsg) {
    this.onerror(errorMsg);
  };

  /**
   * 消息发送（json）
   */
  $.send = function (msgId, msg) {
    if (this.socket) {
      var req = {
        cmd: msgId,
        msg: JSON.stringify(msg),
      };
      this.socket.send(JSON.stringify(req));
      return true;
    }
    this.error("please connect to the server first !!!");
    return false;
  };

  /**
   * 消息发送（二进制）
   */
  $.sendBytes = function (msgId, msg) {
    if (this.socket) {
      let json = JSON.stringify(msg);
      let msgSize = 12 + json.length;
      let buffer = new ArrayBuffer(msgSize);
      const view = new DataView(buffer);
      view.setInt32(0, msgSize);
      view.setInt32(4, 0);
      view.setInt32(8, msgId);

      const jsonBytes = new Uint8Array(new TextEncoder().encode(json));
      const jsonView = new Uint8Array(buffer, 12, jsonBytes.length);
      jsonView.set(jsonBytes);
      this.socket.send(buffer);
      return true;
    }
    this.error("please connect to the server first !!!");
    return false;
  };

  $.close = function () {
    if (this.socket != undefined && this.socket != null) {
      this.socket.close();
    } else {
      this.error("this socket is not available");
    }
  };

  /**
   * 消息回調
   * @param {Object} message
   */
  $.onmessage = function (message) {};

  /**
   * 链接回调函数
   */
  $.onopen = function () {};

  /**
   * 关闭回调
   */
  $.onclose = function () {};

  /**
   * 异常回调
   */
  $.onerror = function () {};
})((ws = {}));
