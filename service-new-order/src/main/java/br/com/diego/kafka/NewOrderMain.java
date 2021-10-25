package br.com.diego.kafka;

import br.com.diego.kafka.dispatcher.KafkaDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderMain.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            for (var i = 0; i < 10; i++) {
                LOGGER.info("GERANDO NOVA VENDA: {}", i);
                var orderId = UUID.randomUUID().toString();
                var amount = Math.random() * 5000 + 1;
                var email = Math.random() + "@email.com";

                var order = new Order(orderId, email, BigDecimal.valueOf(amount));
                orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, new Correlationid(NewOrderMain.class.getSimpleName()), order);
            }
        }
    }
}
