package com.dpaula.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.awt.desktop.UserSessionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Fernando de Lima
 */
public class BathSendMessageService {

    private final Connection connection;

    BathSendMessageService() throws SQLException {

        // conexão simples ao banco
        String url = "jdbc:sqlite:users_database.db";
//        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);

        String sql = " create table Users\n" +
                "                        (\n" +
                "                            uuid varchar(200) primary key,\n" +
                "                            email varchar(200)\n" +
                "                        )";

        try {

            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            //para nao dar erro, pois na segunda vez o banco ja existe
        }
    }

    public static void main(String[] args) throws SQLException {

        var bathService = new BathSendMessageService();

        try (var service = new KafkaService<>(BathSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                bathService::parse,
                Map.of())) {

            service.run();
        }
    }

    /**
     * Corpo da execução da mensagem
     */
    private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException, ExecutionException, InterruptedException {
        final var message = record.value();
        System.out.println("--------------------------------------------------");
        System.out.println("Processando novo batch");
        System.out.println("Tópico " + message.getPayload());

        if(true) throw new RuntimeException("Deu um erro que eu forcei!!!");

        for(User user: getAllUsers()){

            //criando um novo correlationid e concatenando no id das mensagens anteriores
            final var correlationId = message.getId().continueWith(BathSendMessageService.class.getSimpleName());

            userDispatcher.sendAsync(message.getPayload(), user.getUuid(), correlationId, user);
            System.out.println("Acho que enviei para o usuario");
        }

    }

    private List<User> getAllUsers() throws SQLException {

        final var selectUuidFromUsuers = connection.prepareStatement("select uuid from users").executeQuery();

        final var users = new ArrayList<User>();
        while (selectUuidFromUsuers.next()){
            users.add(new User(selectUuidFromUsuers.getString(1)));
        }
        return users;
    }

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();
}
