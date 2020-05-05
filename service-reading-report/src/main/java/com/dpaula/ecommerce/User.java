package com.dpaula.ecommerce;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Fernando de Lima
 */
@Getter
@ToString
public class User {

    private final String uuid;

    public User(String uuid) {
        this.uuid = uuid;
    }

    // retorna local onde sera gerado o relatorio, no target do projeto
    public String getReportPath() {
        return "target/" + uuid + "-report.txt";
    }
}
