package com.studies.catalog.infrastructure.kafka.connect;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageValue<T>(
        @JsonProperty("payload") ValuePayload<T> payload
) {
}
