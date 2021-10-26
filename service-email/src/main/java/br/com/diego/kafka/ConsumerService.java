package br.com.diego.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {
    void parse(ConsumerRecord<String, Message<String>> record);
    String getTopic();
    String getConsumerGroup();
}
