package com.studies.catalog.infrastructure.kafka.connect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Operation {
    CREATE("c"), UPDATE("u"), DELETE("d");

    private final String op;

    Operation(String op) {
        this.op = op;
    }

    @JsonCreator
    public static Operation of(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.op.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public String op() {
        return op;
    }

    public static boolean isDelete(Operation op) {
        return DELETE == op;
    }
}
