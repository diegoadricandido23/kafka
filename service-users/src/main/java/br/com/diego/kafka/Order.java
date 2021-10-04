package br.com.diego.kafka;

import java.math.BigDecimal;

public class Order {
    private final String userId, orderId, email;
    private final BigDecimal amount;

    public Order(String userId, String orderId, String email, BigDecimal amount) {
        this.userId = userId;
        this.orderId = orderId;
        this.email = email;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

}
