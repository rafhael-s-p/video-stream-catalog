package com.studies.catalog.infrastructure.category;

import com.studies.catalog.AbstractElasticsearchTest;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.infrastructure.category.persistence.CategoryDocument;
import com.studies.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CategoryElasticsearchGatewayTest extends AbstractElasticsearchTest {

    @Autowired
    private CategoryElasticsearchGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testInjection() {
        Assertions.assertNotNull(this.categoryGateway);
        Assertions.assertNotNull(this.categoryRepository);
    }

    @Test
    void givenValidCategory_whenCallsSave_shouldPersistIt() {
        // given
        final var movies = Fixture.Categories.movies();

        // when
        final var currentOutput = this.categoryGateway.save(movies);

        // then
        Assertions.assertEquals(movies, currentOutput);

        final var currentCategory = this.categoryRepository.findById(movies.id()).get();

        Assertions.assertEquals(movies.id(), currentCategory.id());
        Assertions.assertEquals(movies.name(), currentCategory.name());
        Assertions.assertEquals(movies.description(), currentCategory.description());
        Assertions.assertEquals(movies.active(), currentCategory.active());
        Assertions.assertEquals(movies.createdAt(), currentCategory.createdAt());
        Assertions.assertEquals(movies.updatedAt(), currentCategory.updatedAt());
        Assertions.assertEquals(movies.deletedAt(), currentCategory.deletedAt());
    }

    @Test
    void givenValidId_whenCallsDeleteById_shouldDeleteIt() {
        // given
        final var movies = Fixture.Categories.movies();

        this.categoryRepository.save(CategoryDocument.from(movies));

        final var expectedId = movies.id();
        Assertions.assertTrue(this.categoryRepository.existsById(expectedId));

        // when
        this.categoryGateway.deleteById(expectedId);

        // then
        Assertions.assertFalse(this.categoryRepository.existsById(expectedId));
    }

    @Test
    void givenInvalidId_whenCallsDeleteById_shouldBeOk() {
        // given
        final var expectedId = "any";

        // when/then
        Assertions.assertDoesNotThrow(() -> this.categoryGateway.deleteById(expectedId));
    }

    @Test
    void givenValidId_whenCallsFindById_shouldRetrieveIt() {
        // given
        final var trailers = Fixture.Categories.trailers();

        this.categoryRepository.save(CategoryDocument.from(trailers));

        final var expectedId = trailers.id();
        Assertions.assertTrue(this.categoryRepository.existsById(expectedId));

        // when
        final var currentOutput = this.categoryGateway.findById(expectedId).get();

        // then
        Assertions.assertEquals(trailers.id(), currentOutput.id());
        Assertions.assertEquals(trailers.name(), currentOutput.name());
        Assertions.assertEquals(trailers.description(), currentOutput.description());
        Assertions.assertEquals(trailers.active(), currentOutput.active());
        Assertions.assertEquals(trailers.createdAt(), currentOutput.createdAt());
        Assertions.assertEquals(trailers.updatedAt(), currentOutput.updatedAt());
        Assertions.assertEquals(trailers.deletedAt(), currentOutput.deletedAt());
    }

    @Test
    void givenInvalidId_whenCallsFindById_shouldReturnEmpty() {
        // given
        final var expectedId = "any";

        // when
        final var currentOutput = this.categoryGateway.findById(expectedId);

        // then
        Assertions.assertTrue(currentOutput.isEmpty());
    }

}
