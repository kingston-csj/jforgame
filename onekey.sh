#!/bin/bash
./admin.sh stop
sleep 2
echo "更新代码并重新进行部署"
./admin.sh update
echo "服务器开始启动..."
./admin.sh start
