package com.dpaula.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public class EmailService {

    public static void main(String[] args) {

        var emailService = new EmailService();
        try(KafkaService<String> service = new KafkaService<>(EmailService.class.getSimpleName(),
                "ECOMMERCE_SEND_EMAIL",
                emailService::parse,
                Map.of())) {

            service.run();
        }
    }

    /**
     * Corpo da execução da mensagem
     */
    private void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("--------------------------------------------------");
        System.out.println("Enviando Email");
        System.out.println("Chave " + record.key());
        System.out.println("Valor " + record.value());
        System.out.println("Partition " + record.partition());
        System.out.println("Offset " + record.offset());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Email foi enviado");
    }
}
