package br.com.diego.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderMain.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
        try (var emailDispatcher = new KafkaDispatcher<String>()) {

                for (var i = 0; i < 10; i++) {
                    var key = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = Math.random() * 5000 +1;

                    var order = new Order(key, orderId, BigDecimal.valueOf(amount));
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", orderId, order);

                    var email = "Thank you for order! We are processing your order";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
                }
            }
        }
    }
}
