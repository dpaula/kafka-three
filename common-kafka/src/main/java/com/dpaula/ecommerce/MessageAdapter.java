package com.dpaula.ecommerce;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author Fernando de Lima
 */
public class MessageAdapter implements JsonSerializer<Message> {

    @Override
    public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {

        final var jsonObject = new JsonObject();

        jsonObject.addProperty("type", message.getPayload().getClass().getName());
        jsonObject.add("payload", context.serialize(message.getPayload()));
        jsonObject.add("correlationId", context.serialize(message.getId()));

//        {
//          "type":"java.lang.String",
//          "payload":"USER_GENERATE_READING_REPORT",
//          "correlationId":{
//                              "id":"f4427e9b-47c0-44bd-8ab2-31e9d43d7ed1"
//                          }
//          }

        return jsonObject;
    }
}
