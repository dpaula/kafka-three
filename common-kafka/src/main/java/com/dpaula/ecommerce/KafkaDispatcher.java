package com.dpaula.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

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

    public void send(String topico, String key, T payload) throws ExecutionException, InterruptedException {

        //encapsulando meu objeto (pauload) dentro da minha mensagem, com id
        final var value = new Message<T>(new CorrelationId(), payload);

        //mensagem que tera a mesma informação, tanto pra chave quanto o valor
        var record = new ProducerRecord<>(topico, key, value);

        // enviando uma mensagem
        // com o .get ele fica sincrono
        producer.send(record, getCallback()).get();
    }

    @Override
    public void close() {
        this.producer.close();
    }
}
