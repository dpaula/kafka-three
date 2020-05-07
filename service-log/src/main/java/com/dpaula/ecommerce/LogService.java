package com.dpaula.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class LogService {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        final var logService = new LogService();
        try (KafkaService<String> service = new KafkaService<>(LogService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE.*"),
                logService::parse,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("--------------------------------------------------");
        System.out.println("LOG: " + record.topic());
        System.out.println("Chave " + record.key());
        System.out.println("Valor " + record.value());
        System.out.println("Partition " + record.partition());
        System.out.println("Offset " + record.offset());
    }
}
