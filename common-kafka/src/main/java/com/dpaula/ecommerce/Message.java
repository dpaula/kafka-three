package com.dpaula.ecommerce;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Fernando de Lima
 */
@Getter
@ToString
public class Message<T> {

    //id da mensgaem
    private final CorrelationId id;
    //objeto da mensagem
    private final T payload;

    public Message(CorrelationId id, T payload) {
        this.id = id;
        this.payload = payload;
    }
}
