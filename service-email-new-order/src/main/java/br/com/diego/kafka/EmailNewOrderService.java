package br.com.diego.kafka;

import br.com.diego.kafka.consumer.KafkaService;
import br.com.diego.kafka.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNewOrderService.class);
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        var emailService = new EmailNewOrderService();
        try (var service = new KafkaService(EmailNewOrderService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                emailService::parse,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING NEW ORDER, PREPARING EMAIL");
        var message = record.value();
        LOGGER.info("MESSAGE: {}", message);

        var emailCode = "Thank you for order! We are processing your order";
        var order = message.getPayload();
        var id = record.value().getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), id, emailCode);

        LOGGER.info("EMAIL ENVIADO COM SUCESSO");
    }
}
