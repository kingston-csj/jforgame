 #!/bin/sh

./onekey.sh stop
./onekey.sh start

tailf log/console.log|while read line
do 
  echo $line
  finished=`echo $line|grep -E "启动成功|关闭成功"|wc -l`
  if [ $finished -eq 1 ]; then 
	kill -9 `ps aux|grep tailf log/console.log|grep -v "grep"|awk '{print $2}'`
  fi
done

