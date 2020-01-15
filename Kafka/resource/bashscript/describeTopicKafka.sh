#!/bin/bash

../bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic user

../bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic order
