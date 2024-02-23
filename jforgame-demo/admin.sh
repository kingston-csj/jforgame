 #!/bin/sh

serverName='GameServer'
GAME_PID=`pwd`/var/game.pid
#echo "pid="$GAME_PID
JMX_IP="10.XX.YY.ZZ"
#JMX_IP=`ifconfig eth0 | grep "inet addr:" |awk '{print $2}' | cut -c 6-`
JMX_PORT="12306"

JVM_ARGS="-Xms1024m -Xmx1024m "
JVM_ARGS="$JVM_ARGS="" -XX:+UseG1GC -XX:ParallelGCThreads=2 -XX:+PrintGCDetails -XX:-OmitStackTraceInFastThrow"
JVM_ARGS="$JVM_ARGS="" -Dcom.sun.management.jmxremote.port="$JMX_PORT
JVM_ARGS="$JVM_ARGS="" -Dcom.sun.management.jmxremote.authenticate=false"
JVM_ARGS="$JVM_ARGS="" -Dcom.sun.management.jmxremote.ssl=false"
JVM_ARGS="$JVM_ARGS="" -Djava.rmi.server.hostname="$JMX_IP

if [ ! -d "var" ]; then
  mkdir "var"
fi
if [ ! -f ${GAME_PID} ]; then 
    touch ${GAME_PID}
fi

if [ $1 == "start" ]; then
  pid=`cat ${GAME_PID}`
  if [ $pid > 0 ]; then  
    echo "server had started"
    exit 0
  fi
  localdir=../../gc
  today=`date +%Y-%m-%d`
  if [ ! -d $localdir ]
  then
    mkdir -p $localdir
  fi
  java -server $JVM_ARGS \
  -Xloggc:$localdir/gc_$today.log \
  -Dgame.serverId=$serverId \
  -Dfile.encoding=UTF-8 -jar $serverName.jar > /dev/null &
    echo $! > ${GAME_PID}
elif [ $1 == "stop" ]; then 
  #pid=`jps -lv|grep serverId=$serverId|awk '{print $1}'`
  pid=`cat ${GAME_PID}`
  if [ $pid > 0 ]; then   
    echo "get ready to close server"
    kill 15 $pid
    rm -f ${GAME_PID}    
    echo "server closed successfully"
  else
    echo "server had not started"
  fi
elif [ $1 == "update" ]; then 
  git pull 
  mvn clean package -DskipTests 
  exit 0
fi
