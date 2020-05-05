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

    public static void main(String[] args) {

        var reportService = new ReadingReportService();

        try (var service = new KafkaService<>(ReadingReportService.class.getSimpleName(),
                "USER_GENERATE_READING_REPORT",
                reportService::parse,
                User.class,
                Map.of())) {

            service.run();
        }
    }

    /**
     * Corpo da execução da mensagem
     *
     */
    private void parse(ConsumerRecord<String, User> record) throws IOException {
        System.out.println("--------------------------------------------------");
        System.out.println("Processando relatório para Usuário: "+record.value());

        var user = record.value();

        var target = new File(user.getReportPath());

        IO.copyTo(SOURCE, target);
        IO.append(target, "Criado para "+user.getUuid());

        System.out.println("Arquivo Criado: "+ target.getAbsolutePath());
    }
}
