
# 版本发布规则
先说下，对于发布的工具，我们还是要遵循基本的版本规则，采用常用的三段式。即
主版本号(Major version).次版本号(Minor version).修订号(Revision number)，如:1.2.3

*主版本号: 版本的主要变更,通常添加新特性会增加主版本号,如从1升级到2.  
*次版本号: 版本的次要变更,修复bug或增加小特性会增加次版本号,如从2.1升级到2.2.  
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

## V1.2.0(2024-06-2)
### jforgame-doctor
    增加對jdk17及以上版本的支持
### jforgame-data
    增加策划配置数据读取工具，支持csv/excel格式，允许数据按id查询，按索引查询，支持数据热更新
    




