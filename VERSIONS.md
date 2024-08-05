
# 版本发布规则
先说下，对于发布的工具，我们还是要遵循基本的版本规则，采用常用的三段式。即
主版本号(Major version).次版本号(Minor version).修订号(Revision number)，如:1.2.3

*主版本号: 版本的主要变更,通常添加新特性（无法向下兼容时）会增加主版本号，如从1升级到2.  
*次版本号: 版本的次要变更,修复bug或增加小特性会增加次版本号,尽量向下兼容，如从2.1升级到2.2.  
*修订号:版本的微小变更,通常修复bug或优化功能会增加修订号,如从2.2.1升级到2.2.2.  




# 版本更新说明
## V1.0.0(2024-03-22)
### jforgame-commons 基本工具类
### jforgame-socket-parent  网络框架,netty+mina
### jforgame-codec-parent  消息编解码,protobuf+reflect


## V1.1.0(2024-03-29)
### jforgame-hotswap 新增代码热更模块
### jforgame-socket-parent  
    新增线程模型接口ThreadModel及其实现DispatchThreadModel
    新增消息处理器注册中心CommonMessageHandlerRegister
### jforgame-socket-netty
    增加websocket简易实现  
### jforgame-socket-struct
    消息javabean支持继承关系，允许编解码父类字段
### jforgame-parent
    所有子模块取消对log4j的全局依赖,log层只绑定sl4j接口  


## V1.2.0(2024-06-16)
### jforgame-socket-netty
    TcpSocketServerBuilder的protocolEncoder改为单例模式，所有客户端共享
### jforgame-doctor
    增加對jdk17及以上版本的支持
### jforgame-data
    新增加策划配置数据读取工具，支持csv/excel格式，允许数据按id查询，按索引查询，支持数据热更新


## V2.0.0(2024-08-01)
### jforgame-socket
    修改私有协议栈格式，消息头增加一个int字段，保存客户端的消息序号。
    废弃Traceable接口，客户端回调id直接使用上面的消息序号。
    注：消息序号不是每个项目都需要，在每个消息增加这样的字段，会导致每个消息多出4个字段的长度。一开始设计是拒绝的，但底层设计不应过于严格，应尽量宽容。
       就像消息类型cmd字段，其实在很多项目里直接申明为short（3w+）也足够用。当然，int的长度允许业务为各种类型进行分段分类。还是那个词，宽容！
       如果实在介意消息序号，或者消息长度，或者需要支持压缩，加密啥的，底层无法做到面面俱到，用户可以参考DefaultProtocolDecoder/DefaultProtocolEncoder,设计自己的私有协议。
       至于RequestDataFrame的MessageHeader类，由于包头字段是具体化设计的，若需要修改，就简单的做法就是在工程里直接拷贝整个文件（包路径不要修改）进行覆盖。
       由于jdk同一个类加载器不会重复相同的class文件，当类加载器首先加载到项目的MessageHeader类，便不会加载jforgame-socket-api依赖的同名文件了！
### jforgame-socket-netty
    websocket支持BinaryWebSocketFrame
### jforgame-socket-mina
    升级mina-core组件： from 2.0.7 to 2.0.22
### jforgame-data
    jforgame-data更名为jforgame-spring-boot-starter-data
    修复DataManager注入失败bug




