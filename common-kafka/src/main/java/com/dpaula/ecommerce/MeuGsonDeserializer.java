package com.dpaula.ecommerce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @author Fernando de Lima
 */
public class MeuGsonDeserializer<T> implements Deserializer<T> {

    public static final String TYPE_CONFIG = "com.dpaula.type_config";
    private final Gson gson = new GsonBuilder().create();
    private Class<T> type;

    /**
     * Método com acesso as configurações definidas
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        //valueOf para não dar nullpointer
        //aqui pegamos o nome completo da classe em string
        String typeConfig = String.valueOf(configs.get(TYPE_CONFIG));

        try {
            this.type = (Class<T>) Class.forName(typeConfig);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Classe: "+typeConfig+" para deserialização, não existe no classpath "+e);
        }
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        return gson.fromJson(new String(bytes), type);
    }
}
