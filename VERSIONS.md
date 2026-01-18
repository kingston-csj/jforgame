# 版本发布规则

对于发布的工具，遵循基本的版本规则，采用常用的三段式。即
主版本号(Major version).次版本号(Minor version).修订号(Revision number)，如:1.2.3

*主版本号: 版本的主要变更，通常添加重大特性，或者代码重构（无法向下兼容），会增加主版本号，如从1.x升级到2.x.  
*次版本号: 版本的次要变更，修复bug或增加小特性会增加次版本号，尽量向下兼容，如从2.1升级到2.2.  
*修订号: 版本的微小变更，通常修复bug或优化功能会增加修订号，如从2.2.1升级到2.2.2.

# 版本更新说明

## V1.0.0(2024-03-22)

### jforgame-commons 基本工具类

### jforgame-socket-parent  网络框架,netty+mina

### jforgame-codec-parent  消息编解码,protobuf+struct

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

    新增加策划配置数据读取工具，支持csv/excel格式，允许数据按id查询，按索引查询，支持数据热更新(此版本不推荐使用，直接升级到2.x)

## V2.0.0(2024-08-01), api变动！！网关客户端代码需要稍微修改

### jforgame-socket

    修改私有协议栈格式，消息头增加一个index字段，保存客户端的消息序号。
    删除Traceable接口，客户端回调id直接使用上面的消息序号。
    注：消息序号不是每个项目都需要，在每个消息增加这样的字段，会导致每个消息多出4个字段的长度。一开始设计是拒绝的，但底层设计不应过于严格，应尽量宽容。
       就像消息类型cmd字段，其实在很多项目里直接申明为short（3w+）也足够用。当然，int的长度允许业务为各种类型进行分段分类。还是那个词，宽容！
       如果实在介意消息序号，或者消息长度，或者需要支持压缩，加密啥的，底层无法做到面面俱到，用户可以参考DefaultProtocolDecoder/DefaultProtocolEncoder,设计自己的私有协议。
       至于RequestDataFrame的MessageHeader类，接口参数及返回值都是int类型，但编辑码层面仍可选择short等类型，节省网络流量。至于应用程序方面的内存消耗，这些消息属于“短命小对象”，对GC影响甚微。

### jforgame-socket-netty

    websocket支持BinaryWebSocketFrame

### jforgame-socket-mina

    升级mina-core版本： from 2.0.7 to 2.0.22, 去掉mina-http依赖

### jforgame-data

    jforgame-data更名为jforgame-spring-boot-starter-data
    修复DataManager注入失败bug
    PTable重命名为DataTable，增加name()方法用于重命名文件
    Container支持使用子类，用于存储二级缓存

## V2.1.1(2024-12-16)

### jforgame-socket-api

    消息处理器如果方法签名有返回值，则无须申明index字段

### jforgame-socket-netty

    修复bug：WebSocketServer二进制流只支持json编解码

### jforgame-codec-struct

    StructMessageCodec新增构造函数，用于设置编码最大字节长度

### jforgame-runtime

    新增模块，用于对应用程序的线程、CPU、内存、gc等数据进行监控

## V2.2.2(2025-2-6)

### jforgame-data

    修复读取excel文件，如果首列数据为空导致数据错乱问题

### jforgame-socket-api

    IdSession类增加sendAndClose方法，用于在主动关闭session前做一些清尾通知

### jforgame-socket-netty

    WebSocketServerBuilder增加idleMilliSeconds参数，若XX时间没收到请求，则服务器主动断开session

## V2.3.0(2025-6-21)

### jforgame-commons

    引入eventbus;DateUtil新增若干方法

### jforgame-spring-boot-starter-data

    excel配置读取改用FileSystemResource；完善表格读取异常日志；增加导出export选项

### jforgame-socket-netty

    websocket增加客户端工具

## V2.4.0(2025-7-28)

### jforgame-commons

    增加trie树结构，用于实现脏词检测
    增加persist工具，用于实现对象异步持久化
    增加MethodCaller工具，基于句柄高性能反射工具
    新增随机工具RandomUtil, RandomWeightPool

### jforgame-socket-netty

    增加websocket帧聚合器，处理大数据请求

### jforgame-socket-parent

    消息处理器使用方法句柄代替传统反射，大幅提升性能

### jforgame-spring-boot-starter-data

    ResourceAutoConfiguration类dataConversionService组件增加ConditionalOnMissingBean注解，允许用户自定义
    引入DataValidator数据完整性验证，目前包括主键及自定义验证规则

### jforgame-hotswap

    修复无法热更新类的bug

## V2.5.0(2025-9-7)

### jforgame-orm

    发布jforgame-orm工具，轻量级，专门为游戏服务器打造的orm工具，支持mysql, sqllite等关系型数据库

### jforgame-doctor

    修复JavaDoctor#hotSwap()返回值有误

### jforgame-data

    增加通用配置项，参考CommonData类，CommonConfig注解相关，通过注解直接引用策划配置项
    org.apache.poi版本从4.1.1升到5.4.0， https://github.com/kingston-csj/jforgame/security/dependabot/34

### jforgame-commons

    新增DigestUtil，ZipUtil, TypeUtil几个工具类
    LruHashMap优化; 修复QueueContainerGroup运行异常。

### jforgame-socket

    完善API文档

### jforgame-socket-netty

    修复WebSocketClient.openSession()方法，确保返回的IdSession是可用的

### jforgame-socket-mina

    mina版本从2.0.22升级到2.0.27， https://github.com/kingston-csj/jforgame/security/dependabot/33

## V2.6.0(2025-10-12)

### jforgame-commons

    修复QueueContainerGroup#name字段显示为空。

### jforgame-socket

    修复MessageTask构造函数初始化问题
    RpcMessageClient增加future请求模式
    TcpSocketClient增加构造函数

### jforgame-orm

    优化SqlFactory的sql语句

### jforgame-socket-netty

    WebSocketServerBuilder增加maxProtocolBytes参数，用于设置最大协议字节数

### jforgame-data

    修复ExcelDataReader,CsvDataReader配置读取规则不统一。
    配置bean支持继承关系，子类可以继承父类的配置字段

## V3.0.0 api变动！！(2025-10-12)

### jforgame-commons

    新增util包，容纳所有工具类, Pair, Triple放到ds包下, FileUtils更名为FileUtil

### jforgame-data

    Container#init()更名为afterLoad(), getRecordsBy()更名为getRecordsByIndex(), getRecord()更名为getRecordById()

### jforgame-orm

    移除SchemaUpdate冗余类

### jforgame-threadmodel

    新增线程模型模块，包含两种实现：1.基于关键字分发模型；2.基于Actor模型

### jforgame-socket

    ServerNode迁移到server目录
    WebSocketJsonFrame移到socket-api模块
    ThreadModel相关接口迁移到threadmodel新模块
    netty/mina 包路径去掉support层
    MessageTask更名为ClientRequestTask，去掉dispatchKey字段
    SocketIoDispatcher#dispatch()参数由RequestDataFrame变更为RequestContext，整合请求的所有上下文信息

## V3.1.0

### jforgame-socket

    调整ServerNode的类路径(API类路径变动！！)

### jforgame-socket-netty

    WebSocketServer增加参数fameType参数，用于设置websocket帧类型

### jforgame-threadmodel

    ThreadModel新增方法isShutdown()，用于判断线程池是否已关闭

### jforgame-data

    DataRepository新增方法queryByUniqueIndex(), 用于根据唯一索引查询单个记录
    Container#inject()方法增加主键检查
    Container增加#validate(DataRepository dataRepository)方法，允许容器进行关联数据校验

### jforgame-commons

    FileUtil新增checkAndCreateDirectory()方法，用于检查并创建目录
### jforgame-hotswap

    修复linux+jdk17热更新失败

### jforgame-codec-struct
    增加MapCodec，以支持字典字段


## V3.2.0

### jforgame-codec-struct

    增加FieldIgnore注解，用于忽略字段序列化

### jforgame-commons
    修复NumberUtil#doubleValue()方法内部错误
    增加schedule.parser包，用于解析游戏常用的自定义格式时间表达式
    EventBus事件监听支持类继承关系，事件分发支持事件继承关系
    修改MethodHandleUtils#createMethodCaller抛出的异常类型

### jforgame-socket-api

    新增Response类，作为客户端响应消息的基类

### jforgame-data
    DataManager修复非小写表名读取失败问题
    修改ForeignKeyValidator触发的提示内容

## V4.0.0 api变动！！(计划)

    Container去掉#validate()无参方法

### jforgame-codec-struct

    StructMessageCodec更名为StructCodec 
    ProtobufMessageCodec更名为ProtobufCodec
    ArrayCodec/CollectionCodec/MapCodec容器元素支持继承关系





