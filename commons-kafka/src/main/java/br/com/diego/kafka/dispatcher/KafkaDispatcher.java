package br.com.diego.kafka.dispatcher;

import br.com.diego.kafka.Correlationid;
import br.com.diego.kafka.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaDispatcher.class);

    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    public void send(String topic, String key, Correlationid id, T payload) throws ExecutionException, InterruptedException {
        final Future<RecordMetadata> future = sendAsync(topic, key, id, payload);
        future.get();
        //O GET SEGURA O CONSUMIDOR
    }

    public Future<RecordMetadata> sendAsync(String topic, String key, Correlationid id, T payload) {
        var value = new Message<>(id.continueWith("_"+topic), payload);
        var record = new ProducerRecord<>(topic, key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                LOGGER.error("ERRO AO ENVIAR: {}", ex.getMessage());
                return;
            }
            LOGGER.info(data.topic() + "::partition " + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
        };

        return producer.send(record, callback);
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    @Override
    public void close() {
        producer.close();
    }
}
