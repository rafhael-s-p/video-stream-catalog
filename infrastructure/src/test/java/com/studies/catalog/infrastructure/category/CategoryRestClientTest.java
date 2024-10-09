package com.studies.catalog.infrastructure.category;

import com.studies.catalog.AbstractRestClientTest;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.domain.exceptions.InternalErrorException;
import com.studies.catalog.infrastructure.category.models.CategoryDTO;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class CategoryRestClientTest extends AbstractRestClientTest {

    @Autowired
    private CategoryRestClient target;

    // OK
    @Test
    void givenACategory_whenReceive200FromServer_shouldBeOk() {
        // given
        final var movies = Fixture.Categories.movies();

        final var responseBody = writeValueAsString(new CategoryDTO(
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

        verify(1, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(movies.id()))));
    }

    // 5XX
    @Test
    void givenACategory_whenReceive5xxFromServer_shouldReturnInternalError() throws IOException {
        // given
        final var expectedId = "123";
        final var expectedErrorMessage = "Error observed from categories [resourceId:%s] [status:500]".formatted(expectedId);

        final var responseBody = writeValueAsString(Map.of("message", "Internal Server Error"));

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

        verify(2, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
    }

    // 404
    @Test
    void givenACategory_whenReceive404NotFoundFromServer_shouldReturnEmpty() throws IOException {
        // given
        final var expectedId = "123";
        final var responseBody = writeValueAsString(Map.of("message", "Not found"));

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

        verify(1, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(expectedId))));
    }

    // Timeout
    @Test
    void givenACategory_whenReceiveTimeout_shouldReturnInternalError() throws IOException {
        // given
        final var movies = Fixture.Categories.movies();
        final var expectedErrorMessage = "Timeout observed from categories [resourceId:%s]".formatted(movies.id());

        final var responseBody = writeValueAsString(new CategoryDTO(
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
                                .withFixedDelay(3000)
                                .withBody(responseBody)
                        )
        );

        // when
        final var currentEx = Assertions.assertThrows(InternalErrorException.class, () -> target.getById(movies.id()));

        // then
        Assertions.assertEquals(expectedErrorMessage, currentEx.getMessage());

        verify(2, getRequestedFor(urlPathEqualTo("/api/categories/%s".formatted(movies.id()))));
    }

    @Test
    public void givenACategory_whenBulkheadIsFull_shouldReturnError() {
        // given
        final var expectedErrorMessage = "Bulkhead 'categories' is full and does not permit further calls";

        acquireBulkheadPermission(CATEGORY);

        // when
        final var currentEx = Assertions.assertThrows(BulkheadFullException.class, () -> target.getById("123"));

        // then
        Assertions.assertEquals(expectedErrorMessage, currentEx.getMessage());

        releaseBulkheadPermission(CATEGORY);
    }
}
