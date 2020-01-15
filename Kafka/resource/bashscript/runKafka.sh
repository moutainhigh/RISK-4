#!/bin/bash

gnome-terminal -e 'bash -c "../bin/zookeeper-server-start.sh ../config/zookeeper.properties "' &

# After running the first command in background (running Zookeeper), we catch the last PID and wait it until it finishs.
# Then, the seccond command will be runned (Kafka server)
PID=$!
wait $PID

gnome-terminal -e 'bash -c "../bin/kafka-server-start.sh ../config/server.properties "'
