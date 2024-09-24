package com.studies.catalog.infrastructure.category;

import com.studies.catalog.AbstractElasticsearchTest;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.domain.category.CategorySearchQuery;
import com.studies.catalog.infrastructure.category.persistence.CategoryDocument;
import com.studies.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

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

    @Test
    void givenEmptyCategories_whenCallsFindAll_shouldReturnEmptyList() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var currentOutput = this.categoryGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, currentOutput.meta().currentPage());
        Assertions.assertEquals(expectedPerPage, currentOutput.meta().perPage());
        Assertions.assertEquals(expectedTotal, currentOutput.meta().total());
        Assertions.assertEquals(expectedTotal, currentOutput.data().size());
    }

    @ParameterizedTest
    @CsvSource({
            "mov,0,10,1,1,Movies",
            "tra,0,10,1,1,Trailers"
    })
    void givenValidTerm_whenCallsFindAll_shouldReturnElementsFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockCategories();

        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var currentOutput = this.categoryGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, currentOutput.meta().currentPage());
        Assertions.assertEquals(expectedPerPage, currentOutput.meta().perPage());
        Assertions.assertEquals(expectedTotal, currentOutput.meta().total());
        Assertions.assertEquals(expectedItemsCount, currentOutput.data().size());
        Assertions.assertEquals(expectedName, currentOutput.data().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,3,3,Movies",
            "name,desc,0,10,3,3,Violence",
            "created_at,asc,0,10,3,3,Movies",
            "created_at,desc,0,10,3,3,Trailers",
    })
    void givenValidSortAndDirection_whenCallsFindAll_shouldReturnElementsSorted(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockCategories();

        final var expectedTerms = "";

        final var aQuery =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var currentOutput = this.categoryGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, currentOutput.meta().currentPage());
        Assertions.assertEquals(expectedPerPage, currentOutput.meta().perPage());
        Assertions.assertEquals(expectedTotal, currentOutput.meta().total());
        Assertions.assertEquals(expectedItemsCount, currentOutput.data().size());
        Assertions.assertEquals(expectedName, currentOutput.data().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,1,3,Movies",
            "1,1,1,3,Trailers",
            "2,1,1,3,Violence",
            "3,1,0,3,",
    })
    void givenValidPage_whenCallsFindAll_shouldReturnElementsPaged(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockCategories();

        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var currentOutput = this.categoryGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, currentOutput.meta().currentPage());
        Assertions.assertEquals(expectedPerPage, currentOutput.meta().perPage());
        Assertions.assertEquals(expectedTotal, currentOutput.meta().total());
        Assertions.assertEquals(expectedItemsCount, currentOutput.data().size());

        if (StringUtils.isNotEmpty(expectedName)) {
            Assertions.assertEquals(expectedName, currentOutput.data().get(0).name());
        }
    }

    private void mockCategories() {
        this.categoryRepository.save(CategoryDocument.from(Fixture.Categories.movies()));
        this.categoryRepository.save(CategoryDocument.from(Fixture.Categories.violence()));
        this.categoryRepository.save(CategoryDocument.from(Fixture.Categories.trailers()));
    }

}
