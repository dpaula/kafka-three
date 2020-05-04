package com.dpaula.ecommerce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {

                for (var i = 0; i < 10; i++) {

                    var orderId = UUID.randomUUID().toString();
                    // numero decimal entre 1 e 5000
                    var amount = new BigDecimal(Math.random() * 5000 + 1);

                    var email = Math.random() + "@gmail.com";

                    var order = new Order(orderId, amount, email);

                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

                    var emailCode = "Obrigado pelo pedido! Estamos processando seu pedido!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
                }
            }
        }
    }
}
