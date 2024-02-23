 #!/bin/sh
./admin.sh stop
sleep 2
echo "update code"
./admin.sh update
echo "start server..."
./admin.sh start
