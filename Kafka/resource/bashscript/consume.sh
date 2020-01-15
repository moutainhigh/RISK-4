#!/bin/bash

for ((i=1;i<=3;i++))
do
gnome-terminal -e 'bash -c "python3 ../python/consumerUser.py; exec bash"'
done

for ((i=1;i<=3;i++))
do
gnome-terminal -e 'bash -c "python3 ../python/consumerOrder.py; exec bash"'
done
