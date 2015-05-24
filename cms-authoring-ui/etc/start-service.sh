#! /bin/bash
echo "Starting service..."
nohup java -jar cms-authoring-ui-1.0-SNAPSHOT.jar > /dev/null 2>&1 &
echo $! > /tmp/cms-authoring-service.pid
sleep 1

