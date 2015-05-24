#! /bin/bash
echo "Starting service..."
nohup java -jar cms-registration-service-1.0-SNAPSHOT.jar > /dev/null 2>&1 &
echo $! > /tmp/cms-registration-service.pid
sleep 1

