package com.dpaula.ecommerce;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Fernando de Lima
 */
class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;

    KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private static Callback getCallback() {
        return (dadosSucesso, excpFalha) -> {
            //callback para tratar o retonro sincrono

            if (excpFalha != null) {
                excpFalha.printStackTrace();
                return;
            }
            System.out.println("Sucesso enviando : " + dadosSucesso.topic() + ":::partition " + dadosSucesso.partition() + "/ offset " + dadosSucesso.offset() + "/ timestamp " + dadosSucesso.timestamp());
        };
    }

    // criando as propriedades na mão, mas deve ser pelo arquivo de properties
    private Properties properties() {

        var properties = new Properties();

        //setando o endereço do kafka
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        //Informando qual classe de serialização será usada para chave, neste caso sera string
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //Informando qual classe de serialização será usada para o valor, neste caso usando uma customizada
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MeuGsonSerializer.class.getName());
        //Garante que todas as replicas (brokers) irao ficar atualizadas com as mensagens
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");

        return properties;
    }

    //confirma que a mensagem foi enviada corretamente
    public void send(String topico, String key, CorrelationId correlationId, T payload) throws ExecutionException, InterruptedException {
        final Future<RecordMetadata> future = sendAsync(topico, key, correlationId, payload);

        // com o .get ele fica sincrono
                future.get();
    }

    //não retorna a confirmação que a mensagem foi enviada
    public Future<RecordMetadata> sendAsync(String topico, String key, CorrelationId correlationId, T payload) {
        //encapsulando meu objeto (pauload) dentro da minha mensagem, com id
        final var value = new Message<T>(correlationId, payload);

        //mensagem que tera a mesma informação, tanto pra chave quanto o valor
        var record = new ProducerRecord<>(topico, key, value);

        // enviando uma mensagem
        return producer.send(record, getCallback());
    }

    @Override
    public void close() {
        this.producer.close();
    }
}
