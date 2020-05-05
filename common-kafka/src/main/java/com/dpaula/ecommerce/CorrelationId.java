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

    public CorrelationId() {
        this.id = UUID.randomUUID().toString();
    }
}
