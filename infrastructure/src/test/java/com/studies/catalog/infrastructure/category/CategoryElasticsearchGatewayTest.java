package com.studies.catalog.infrastructure.category;

import com.studies.catalog.AbstractElasticsearchTest;
import com.studies.catalog.domain.Fixture;
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
        Assertions.assertNotNull(categoryRepository);
    }

    @Test
    void givenValidCategory_whenCallsSave_shouldPersistIt() {
        // given
        final var movies = Fixture.Categories.movies();

        // when
        final var currentOutput = this.categoryGateway.save(movies);

        // then
        Assertions.assertEquals(movies, currentOutput);

        final var currentCategory = categoryRepository.findById(movies.id()).get();

        Assertions.assertEquals(movies.id(), currentCategory.id());
        Assertions.assertEquals(movies.name(), currentCategory.name());
        Assertions.assertEquals(movies.description(), currentCategory.description());
        Assertions.assertEquals(movies.active(), currentCategory.active());
        Assertions.assertEquals(movies.createdAt(), currentCategory.createdAt());
        Assertions.assertEquals(movies.updatedAt(), currentCategory.updatedAt());
        Assertions.assertEquals(movies.deletedAt(), currentCategory.deletedAt());
    }
}
