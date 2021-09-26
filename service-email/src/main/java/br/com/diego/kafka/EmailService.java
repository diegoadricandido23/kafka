package br.com.diego.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public static void main(String[] args) {
        var emailService = new EmailService();
        try (final KafkaService kafkaService = new KafkaService(
                EmailService.class.getSimpleName(),
                "ECOMMERCE_SEND_EMAIL",
                emailService::parse,
                String.class,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
        LOGGER.info("-------------------------");
        LOGGER.info("SENDING EMAIL, CHECKING FOR FRAUD");
        LOGGER.info("RECORD KEY: {}", record.key());//DEFINI EM QUAL PARTICAO IRA CAIR A MENSAGEM
        LOGGER.info("RECORD VAL: {}", record.value());
        LOGGER.info("PARTITION : {}", record.partition());
        LOGGER.info("OFFSET    : {}", record.offset());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("PROCESSADO COM SUCESSO");

    }
}
