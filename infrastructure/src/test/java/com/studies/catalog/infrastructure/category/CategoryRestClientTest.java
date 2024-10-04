package com.studies.catalog.infrastructure.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studies.catalog.IntegrationTestConfiguration;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.domain.exceptions.InternalErrorException;
import com.studies.catalog.infrastructure.category.models.CategoryDTO;
import com.studies.catalog.infrastructure.configuration.WebServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Tag("integrationTest")
@ActiveProfiles("integration-test")
@AutoConfigureWireMock(port = 0)
@EnableAutoConfiguration(exclude = {
        ElasticsearchRepositoriesAutoConfiguration.class,
        KafkaAutoConfiguration.class,
})
@SpringBootTest(classes = {WebServerConfig.class, IntegrationTestConfiguration.class})
class CategoryRestClientTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRestClient target;

    // OK
    @Test
    void givenACategory_whenReceive200FromServer_shouldBeOk() throws IOException {
        // given
        final var movies = Fixture.Categories.movies();

        final var responseBody = objectMapper.writeValueAsString(new CategoryDTO(
                movies.id(),
                movies.name(),
                movies.description(),
                movies.active(),
                movies.createdAt(),
                movies.updatedAt(),
                movies.deletedAt()
        ));

        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(movies.id())))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                        )
        );

        // when
        final var currentCategory = target.getById(movies.id()).get();

        // then
        Assertions.assertEquals(movies.id(), currentCategory.id());
        Assertions.assertEquals(movies.name(), currentCategory.name());
        Assertions.assertEquals(movies.description(), currentCategory.description());
        Assertions.assertEquals(movies.active(), currentCategory.active());
        Assertions.assertEquals(movies.createdAt(), currentCategory.createdAt());
        Assertions.assertEquals(movies.updatedAt(), currentCategory.updatedAt());
        Assertions.assertEquals(movies.deletedAt(), currentCategory.deletedAt());
    }

    // 5XX
    @Test
    void givenACategory_whenReceive5xxFromServer_shouldReturnInternalError() throws IOException {
        // given
        final var expectedId = "123";
        final var expectedErrorMessage = "Failed to get Category of id %s".formatted(expectedId);

        final var responseBody = objectMapper.writeValueAsString(Map.of("message", "Internal Server Error"));

        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(expectedId)))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                        )
        );

        // when
        final var currentEx = Assertions.assertThrows(InternalErrorException.class, () -> target.getById(expectedId));

        // then
        Assertions.assertEquals(expectedErrorMessage, currentEx.getMessage());
    }

    // 404
    @Test
    void givenACategory_whenReceive404NotFoundFromServer_shouldReturnEmpty() throws IOException {
        // given
        final var expectedId = "123";
        final var responseBody = objectMapper.writeValueAsString(Map.of("message", "Not found"));

        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(expectedId)))
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                        )
        );

        // when
        final var currentCategory = target.getById(expectedId);

        // then
        Assertions.assertTrue(currentCategory.isEmpty());
    }

    // Timeout
    @Test
    void givenACategory_whenReceiveTimeout_shouldReturnInternalError() throws IOException {
        // given
        final var movies = Fixture.Categories.movies();
        final var expectedErrorMessage = "Timeout from category of ID %s".formatted(movies.id());

        final var responseBody = objectMapper.writeValueAsString(new CategoryDTO(
                movies.id(),
                movies.name(),
                movies.description(),
                movies.active(),
                movies.createdAt(),
                movies.updatedAt(),
                movies.deletedAt()
        ));


        stubFor(
                get(urlPathEqualTo("/api/categories/%s".formatted(movies.id())))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withFixedDelay(600)
                                .withBody(responseBody)
                        )
        );

        // when
        final var currentEx = Assertions.assertThrows(InternalErrorException.class, () -> target.getById(movies.id()));

        // then
        Assertions.assertEquals(expectedErrorMessage, currentEx.getMessage());
    }
}
