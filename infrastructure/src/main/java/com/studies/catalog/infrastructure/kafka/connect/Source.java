package com.studies.catalog.infrastructure.kafka.connect;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Source(
        @JsonProperty("name") String name,
        @JsonProperty("db") String database,
        @JsonProperty("table") String table
) {
}
