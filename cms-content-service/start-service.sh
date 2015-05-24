#! /bin/bash
echo "Starting service..."
nohup java -jar cms-content-service-1.0-SNAPSHOT.jar > /dev/null 2>&1 &
echo $! > /tmp/service.pid
sleep 1

