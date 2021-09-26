package br.com.diego.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FraudDetectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class);

    public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        try (var service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING NEW ORDER, CHECKING FOR FRAUD");
        LOGGER.info("RECORD KEY: {}", record.key());//DEFINE EM QUAL PARTICAO IRA CAIR A MENSAGEM
        LOGGER.info("RECORD VAL: {}", record.value());
        LOGGER.info("PARTITION : {}", record.partition());
        LOGGER.info("OFFSET    : {}", record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("PROCESSADO COM SUCESSO");
    }
}
