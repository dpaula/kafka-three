package com.dpaula.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * @author Fernando de Lima
 */
public class KafkaService<T> implements Closeable {
    private final ConsumerFunction parse;
    private final Class<T> type;
    private final Map<String, String> overrideProperties;
    private final KafkaConsumer<String, T> consumer;
    private String grupoId;

    KafkaService(String grupoId, String topico, ConsumerFunction parse, Class<T> type, Map<String, String> propriedadesEstras) {
        this.grupoId = grupoId;
        this.parse = parse;
        this.type = type;
        this.overrideProperties = propriedadesEstras;
        //configurando consumidor de mensagens, cuja chave e valor sejam String, com as propriedades de configuração
        this.consumer = new KafkaConsumer<>(properties());

        List<String> topicos = Collections.singletonList(topico);

        // definindo qual será o tópico que o consumidor ficara escutando
        consumer.subscribe(topicos);

    }

    KafkaService(String grupoId, Pattern topico, ConsumerFunction parse, Class<T> type, Map<String, String> propriedadesEstras) {
        this.grupoId = grupoId;
        this.parse = parse;
        this.type = type;
        this.overrideProperties = propriedadesEstras;
        //configurando consumidor de mensagens, cuja chave e valor sejam String, com as propriedades de configuração
        this.consumer = new KafkaConsumer<>(properties());

        // definindo qual será o tópico que o consumidor ficara escutando
        consumer.subscribe(topico);

    }

    private Properties properties() {

        var properties = new Properties();

        //setando o endereço do kafka
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9091");
        //Informando qual classe de DEserialização será usada para chave, neste caso sera string
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //Informando qual classe de DEserialização será usada para o valor, neste caso sera string
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MeuGsonDeserializer.class.getName());
        // definindo o id do grupo consumidor
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, grupoId);
        // definindo um id para o consumidor
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, grupoId + "-" + UUID.randomUUID().toString());

        // definindo a nossa propriedade para definir o tipo que sera deserealizado
        properties.setProperty(MeuGsonDeserializer.TYPE_CONFIG, type.getName());

        // definindo as propriedades que deseja sobreescrever
        properties.putAll(overrideProperties);

        return properties;
    }

    void run() {
        while (true) {
            // perguntando se tem mensagem no tópico por 100 ms
            var records = consumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) {
                continue;
            }
            System.out.println("Encontrei " + records.count() + " registros");

            for (var record : records) {
                try {
                    parse.consume(record);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
