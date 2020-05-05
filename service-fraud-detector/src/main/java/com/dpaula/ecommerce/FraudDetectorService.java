package com.dpaula.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {

        var fraudService = new FraudDetectorService();

        try (KafkaService<Order> service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                Order.class,
                Map.of())) {

            service.run();
        }
    }

    /**
     * Corpo da execução da mensagem
     */
    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("--------------------------------------------------");
        System.out.println("Processando novo pedido, verificando por fraude");
        System.out.println("Chave " + record.key());
        System.out.println("Valor " + record.value());
        System.out.println("Partition " + record.partition());
        System.out.println("Offset " + record.offset());
        final var message = record.value();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final var order = message.getPayload();

        //criando um novo correlationid e concatenando no id das mensagens anteriores
        final var correlationId = message.getId().continueWith(FraudDetectorService.class.getSimpleName());

        if (ehFraude(order)) {
            // caso o valor do pedido for maior ou igual a 4500, entao é como se fosse fraude
            System.out.println("Pedido detectado como FRAUDE!");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), correlationId, order);
        } else {
            System.out.println("Pedido aprovado: " + order);
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), correlationId, order);
        }

        System.out.println("Pedido processado");
    }

    private boolean ehFraude(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}
