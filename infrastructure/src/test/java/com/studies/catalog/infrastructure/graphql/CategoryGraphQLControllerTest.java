package com.studies.catalog.infrastructure.graphql;

import com.studies.catalog.application.category.list.ListCategoryOutput;
import com.studies.catalog.application.category.list.ListCategoryUseCase;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.domain.category.CategorySearchQuery;
import com.studies.catalog.domain.pagination.Pagination;
import com.studies.catalog.infrastructure.GraphQLControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@GraphQLControllerTest
class CategoryGraphQLControllerTest {

    @MockBean
    private ListCategoryUseCase listCategoryUseCase;

    @Autowired
    private GraphQlTester graphql;

    @Test
    void givenDefaultArgumentsWhenCallsListCategoriesShouldReturnIt() {
        // given
        final var expectedCategories = List.of(
                ListCategoryOutput.from(Fixture.Categories.movies()),
                ListCategoryOutput.from(Fixture.Categories.trailers())
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedSearch = "";

        when(this.listCategoryUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedCategories.size(), expectedCategories));

        final var query = """
                {
                  categories {
                    id
                    name
                  }
                }
                """;

        // when
        final var response = this.graphql.document(query).execute();

        final var currentCategories = response.path("categories")
                .entityList(ListCategoryOutput.class)
                .get();

        // then
        Assertions.assertEquals(currentCategories.size(), expectedCategories.size());
        Assertions.assertTrue(currentCategories.containsAll(expectedCategories));

        final var capturer = ArgumentCaptor.forClass(CategorySearchQuery.class);

        verify(this.listCategoryUseCase, times(1)).execute(capturer.capture());

        final var currentQuery = capturer.getValue();
        Assertions.assertEquals(expectedPage, currentQuery.page());
        Assertions.assertEquals(expectedPerPage, currentQuery.perPage());
        Assertions.assertEquals(expectedSort, currentQuery.sort());
        Assertions.assertEquals(expectedDirection, currentQuery.direction());
        Assertions.assertEquals(expectedSearch, currentQuery.terms());
    }

    @Test
    void givenCustomArgumentsWhenCallsListCategoriesShouldReturnIt() {
        // given
        final var expectedCategories = List.of(
                ListCategoryOutput.from(Fixture.Categories.movies()),
                ListCategoryOutput.from(Fixture.Categories.trailers())
        );

        final var expectedPage = 2;
        final var expectedPerPage = 15;
        final var expectedSort = "id";
        final var expectedDirection = "desc";
        final var expectedSearch = "asd";

        when(this.listCategoryUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedCategories.size(), expectedCategories));

        final var query = """
                {
                  categories(search: "%s", page: %s, perPage: %s, sort: "%s", direction: "%s") {
                    id
                    name
                  }
                }
                """.formatted(expectedSearch, expectedPage, expectedPerPage, expectedSort, expectedDirection);

        // when
        final var response = this.graphql.document(query).execute();

        final var currentCategories = response.path("categories")
                .entityList(ListCategoryOutput.class)
                .get();

        // then
        Assertions.assertEquals(currentCategories.size(), expectedCategories.size());
        Assertions.assertTrue(currentCategories.containsAll(expectedCategories));

        final var capturer = ArgumentCaptor.forClass(CategorySearchQuery.class);

        verify(this.listCategoryUseCase, times(1)).execute(capturer.capture());

        final var currentQuery = capturer.getValue();
        Assertions.assertEquals(expectedPage, currentQuery.page());
        Assertions.assertEquals(expectedPerPage, currentQuery.perPage());
        Assertions.assertEquals(expectedSort, currentQuery.sort());
        Assertions.assertEquals(expectedDirection, currentQuery.direction());
        Assertions.assertEquals(expectedSearch, currentQuery.terms());
    }
}
