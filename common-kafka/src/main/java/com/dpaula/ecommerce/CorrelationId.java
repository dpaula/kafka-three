package com.dpaula.ecommerce;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Fernando de Lima
 *
 * Id do rastreamento das mensagens
 */
@Getter
@ToString
public class CorrelationId {

    private final String id;

    //criando um correlationid com o nome da classe de quem enviou a mensagem e um id
    public CorrelationId(String title) {
        this.id = title + "("+ UUID.randomUUID().toString()+ ")";
    }

    // concatenando novo id da nova mensagem, com o id antigo, chaveando as mensagens
    public CorrelationId continueWith(String title) {
        return new CorrelationId(id + "-"+title);
    }
}
