package br.com.diego.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ServiceProvider<T> implements Callable<Void> {
    private ServiceFactory<T> factory;
    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    public Void call() throws SQLException, ExecutionException, InterruptedException {
        var myService = factory.create();
        try (final KafkaService kafkaService = new KafkaService(
                myService.getConsumerGroup(),
                myService.getTopic(),
                myService::parse,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
            kafkaService.run();
        }
        return null;
    }
}
