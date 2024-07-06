#!/bin/bash

serverId=1
serverPort=8081
activeProfile=test

server_pid() {
  echo `jps -lv|grep GameServer|awk '{print $1}'`
}
stop() {
  pid=$(server_pid)
  if [ -z $pid ]; then
        echo "app web stopped"
  else
        echo "app web is running, prepared to stop"
	kill -9 $pid
	echo "app web stopped successfully"
  fi
}
# 稍等片刻

JMX_PORT=8099
JMX_IP="192.168.0.120"
JVM_ARGS="-Xms1024m -Xmx1024m "
JVM_ARGS="$JVM_ARGS"" -XX:+UseZGC"
JVM_ARGS="$JVM_ARGS"" -Djdk.attach.allowAttachSelf"
JVM_ARGS="$JVM_ARGS"" -XX:+HeapDumpOnOutOfMemoryError -XX:ErrorFile=./logs/java_error_%p.log"
JVM_ARGS="$JVM_ARGS"" -Dcom.sun.management.jmxremote.port="$JMX_PORT
JVM_ARGS="$JVM_ARGS"" -Dcom.sun.management.jmxremote.rmi.port="$JMX_PORT
JVM_ARGS="$JVM_ARGS"" -Dcom.sun.management.jmxremote.authenticate=false"
JVM_ARGS="$JVM_ARGS"" -Dcom.sun.management.jmxremote.ssl=false"
JVM_ARGS="$JVM_ARGS"" -Dcom.sun.management.jmxremote.access.file=/home/jmx/jmxremote.access"
JVM_ARGS="$JVM_ARGS"" -Dcom.sun.management.jmxremote.password.file=/home/jmx/jmxremote.password"
JVM_ARGS="$JVM_ARGS"" -Djava.rmi.server.hostname="$JMX_IP

start() {
  local pid=$(server_pid)
  if [ -n "$pid" ]; then
	echo "server is running now"
    	return
  fi
  nohup java  $JVM_ARGS  -jar GameServer.jar  --spring.profiles.active=$activeProfile --server.port=$serverPort >  output.txt 2>&1  &
  sleep 3
  pid=`jps -lv|grep GameServer|awk '{print $1}'`
  echo "server started sucessfully!"
  echo “server is running, pid = $pid”
  md5=`md5sum GameServer.jar|awk '{print $1}'`
  echo "exec ok, date = `date`, md5=$md5"  > result.txt
}

status() {
  local pid=$(server_pid)
  if [ -z $pid ]; then
        echo "server$serverId status: dead"
  else
        echo "server$serverId status: running"
	echo "jvm_params: $JVM_ARGS"
  fi
}

help() {
  echo "--stop: stop the web server if is running"
  echo "--start: start the web server if isnot running, return otherwise "
  echo "--restart: stop the old server before staring"
  echo "--status: show sever status, inclduing running staus and base jvm info"
}

if [ $1 == "start" ]; then
	start
elif [ $1 == "stop" ]; then
    	stop
elif [ $1 == "restart" ]; then
	stop
	echo "prepare to start server"
	sleep 3
	start
elif [ $1 == "status" ]; then
	status
elif [ $1 == "help" ]; then
	help
else
	echo "illegal params, use help for more information"
fi
