#!/bin/bash

gnome-terminal -e 'bash -c "../bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic user"'

gnome-terminal -e 'bash -c "../bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic order"'




