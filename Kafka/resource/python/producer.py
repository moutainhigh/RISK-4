from kafka import KafkaProducer
from kafka.errors import KafkaError
import time

brokers  = ['localhost:9092']
producer = KafkaProducer(bootstrap_servers=brokers)

def on_send_success(record_metadata):
    print(record_metadata.topic)
    print(record_metadata.partition)
    print(record_metadata.offset)

def on_send_error(excp):
    print('Error')

dataUser = []
for i in range(0,200):   
    dataUser.append([])
    dataUser[i].append('user')
    dataUser[i].append('user'+str(i))

dataOrder = []
for i in range(0,200):   
    dataOrder.append([])
    dataOrder[i].append('order')
    dataOrder[i].append('order'+str(i))

for i in range(0,200):
    producer.send(dataUser[i][0], dataUser[i][1].encode())
    time.sleep(1/1000)
    producer.send(dataOrder[i][0], dataOrder[i][1].encode())
    time.sleep(1/1000)