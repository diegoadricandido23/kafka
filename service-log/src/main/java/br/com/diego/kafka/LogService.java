package br.com.diego.kafka;

import br.com.diego.kafka.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class LogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        var logService = new LogService();
        try (var service = new KafkaService(LogService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE.*"),
                logService::parse,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) {
        LOGGER.info("-------------------------");
        LOGGER.info("LOG: {}", record.topic());
        LOGGER.info(record.key());
        LOGGER.info(String.valueOf(record.value()));
        LOGGER.info(String.valueOf(record.partition()));
        LOGGER.info(String.valueOf(record.offset()));
    }
}
