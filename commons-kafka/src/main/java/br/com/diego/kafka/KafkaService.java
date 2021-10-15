package br.com.diego.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

class KafkaService<T> implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);
    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction parse;

    KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Map<String, String> props) {
        this(parse, groupId, props);
        consumer.subscribe(Collections.singletonList(topic));

    }

    KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> props) {
        this(parse, groupId, props);
        consumer.subscribe(topic);

    }

    private KafkaService(ConsumerFunction<T> parse, String groupId, Map<String, String> props) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId, props));

    }

    void run() throws ExecutionException, InterruptedException, SQLException {
        try(var deadLetter = new KafkaDispatcher<>()) {
            while (true) {
                var records = consumer.poll(Duration.ofMillis(100));
                if (!records.isEmpty()) {
                    LOGGER.info("FOUND {} RECORDS", records.count());
                    records.forEach(record -> {
                        try {
                            parse.consume(record);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage());
                            var message = record.value();
                            try {
                                deadLetter.send("ECOMMERCE_DEADLETTER",
                                        message.getId().toString(),
                                        message.getId().continueWith("DEADLETTER"),
                                        new GsonSerializer().serialize("", message));
                            } catch (ExecutionException | InterruptedException ex) {
                                LOGGER.error(ex.getMessage());
                                throw new RuntimeException(ex);
                            }
                        }

                    });
                }
            }
        }
    }

    private  Properties getProperties(String groupId, Map<String, String> overrideProps) {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.putAll(overrideProps);
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
