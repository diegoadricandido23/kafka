package br.com.diego.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class);
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        try (var service = new KafkaService(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING NEW ORDER, CHECKING FOR FRAUD");
        LOGGER.info("RECORD KEY: {}", record.key());//DEFINE EM QUAL PARTICAO IRA CAIR A MENSAGEM
        LOGGER.info("RECORD VAL: {}", record.value());
        LOGGER.info("PARTITION : {}", record.partition());
        LOGGER.info("OFFSET    : {}", record.offset());

        var message = record.value();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var order = message.getPayload();
        if(isFraud(order)) {
            LOGGER.info("ORDER IS A FRAUD: ", order);
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED",
                    order.getEmail(),
                    message.contiueWith(FraudDetectorService.class.getSimpleName()),
                    order);
        } else {
            LOGGER.info("APPROVED: {}", order);
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED",
                    order.getEmail(),
                    message.contiueWith(FraudDetectorService.class.getSimpleName()),
                    order);
        }

        LOGGER.info("PROCESSADO COM SUCESSO");
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500"))  >= 0;
    }
}
