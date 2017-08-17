 #!/bin/sh
serverId="001"
JVM_ARGS='-Xms1024m -Xmx1024m -Xmn512m -XX:MaxTenuringThreshold=3 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:ParallelGCThreads=2 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationStoppedTime -XX:-OmitStackTraceInFastThrow -XX:+PrintTenuringDistribution -Dgame.serverId=001 ' 
if [ $1 == "start" ]; then
  cd target/
  serverName='GameServer'
  localdir=../../gc
  today=`date +%Y-%m-%d`
  if [ ! -d $localdir ]
  then
    mkdir -p $localdir
  fi
  java -server ${JVM_ARGS} \
  -Xloggc:$localdir/gc_$today.log \
  -Dgame.serverId=$serverId \
  -Dfile.encoding=UTF-8 -jar $serverName.jar > /dev/null &
elif [ $1 == "stop" ]; then 
  pid=`jps -lv|grep serverId=$serverId|awk '{print $1}'`
  if [ $pid > 0 ]; then   
     echo "准备关闭服务器"
     kill 15 $pid
     echo "游戏正常关闭"
  fi
elif [ $1 == "update" ]; then 
  git pull 
  mvn clean package -DskipTests 
exit 0
fi
