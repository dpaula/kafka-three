package com.dpaula.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportService {

    // local do arquivo
    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        var reportService = new ReadingReportService();

        try (var service = new KafkaService<>(ReadingReportService.class.getSimpleName(),
                "ECOMMERCE_USER_GENERATE_READING_REPORT",
                reportService::parse,
                Map.of())) {

            service.run();
        }
    }

    /**
     * Corpo da execução da mensagem
     *
     */
    private void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        var message = record.value();

        System.out.println("--------------------------------------------------");
        System.out.println("Processando relatório para Usuário: "+record.value());

        final var user = message.getPayload();

        var target = new File(user.getReportPath());

        IO.copyTo(SOURCE, target);
        IO.append(target, "Criado para "+user.getUuid());

        System.out.println("Arquivo Criado: "+ target.getAbsolutePath());
    }
}
