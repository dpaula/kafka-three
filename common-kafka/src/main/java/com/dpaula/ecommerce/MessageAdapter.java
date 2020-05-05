package com.dpaula.ecommerce;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author Fernando de Lima
 *
 * Serializa e desserializa as mensagens
 */
public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    /**
     * Serializando uma mensagem com seu id e seu objeto, e retornando um json
     */
    @Override
    public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {

        //criando um json vazio
        final var jsonObject = new JsonObject();

        //incluindo o tipo do objeto (nome da classe)
        jsonObject.addProperty("type", message.getPayload().getClass().getName());
        //incluindo o objeto
        jsonObject.add("payload", context.serialize(message.getPayload()));
        //incluindo o id da mensagem
        jsonObject.add("correlationId", context.serialize(message.getId()));

//        {
//          "type":"java.lang.String",
//          "payload":"USER_GENERATE_READING_REPORT",
//          "correlationId":{
//                              "id":"f4427e9b-47c0-44bd-8ab2-31e9d43d7ed1"
//                          }
//          }

        //retornando o json serializado
        return jsonObject;
    }

    /**
     * Desserializando um json, e retornando uma mensagem, com seu objeto e seu id
     */
    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        //cria um objeto a partir do json
        final var jsonObject = json.getAsJsonObject();

        //extrai o tipo do objeto na mensagem
        final var payloadType = jsonObject.get("type").getAsString();
        //extrai o id da mensagem
        final var correlationId = (CorrelationId) context.deserialize(jsonObject.get("correlationId"), CorrelationId.class);
        try {
            //extrai o objeto na mensagem a partir do tipo
            //pode ocorrer erro, pois a classe pode nao existir
            final var payload = context.deserialize(jsonObject.get("payload"), Class.forName(payloadType));

            //retornando a mensagem desserializada
            return new Message(correlationId, payload);

        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
