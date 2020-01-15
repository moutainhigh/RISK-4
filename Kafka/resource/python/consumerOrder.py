from kafka import KafkaConsumer

brokers = ['localhost:9092']
consumer = KafkaConsumer('order', group_id="group_order", bootstrap_servers=brokers,  auto_offset_reset='earliest')

for msg in consumer: 
    print ("%s:%d:%d: key=%s value=%s" % (msg.topic, msg.partition,msg.offset, msg.key,msg.value))