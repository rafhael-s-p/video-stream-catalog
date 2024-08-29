package com.studies.catalog.application.category.list;

import com.studies.catalog.application.UseCaseTest;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.domain.category.CategoryGateway;
import com.studies.catalog.domain.category.CategorySearchQuery;
import com.studies.catalog.domain.pagination.Pagination;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ListCategoryUseCaseTest extends UseCaseTest {

    @InjectMocks
    private ListCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    void givenValidQuery_whenCallsListCategories_shouldReturnCategories() {
        // given
        final var categories = List.of(
                Fixture.Categories.movies(),
                Fixture.Categories.trailers()
        );

        final var expectedItems = categories.stream()
                .map(ListCategoryOutput::from)
                .toList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Mov";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 2;

        final var aQuery =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var pagination =
                new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        when(this.categoryGateway.findAll(any()))
                .thenReturn(pagination);

        // when
        final var currentOutput = this.useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, currentOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, currentOutput.perPage());
        Assertions.assertEquals(expectedItemsCount, currentOutput.items().size());
        Assertions.assertEquals(expectedItems.size(), currentOutput.items().size());
        Assertions.assertTrue(expectedItems.containsAll(currentOutput.items())
        );
    }
}
