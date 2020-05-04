package com.dpaula.ecommerce;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author Fernando de Lima
 */
@Getter
@ToString
public class Order {

    private final String orderId;
    private final BigDecimal amount;
    private final String email;

    public Order(String orderId, BigDecimal amount, String email) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }
}
