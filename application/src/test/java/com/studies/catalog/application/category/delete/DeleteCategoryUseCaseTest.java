package com.studies.catalog.application.category.delete;

import com.studies.catalog.application.UseCaseTest;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.domain.category.CategoryGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DeleteCategoryUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DeleteCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    void givenValidId_whenCallsDelete_shouldBeOk() {
        // given
        final var movies = Fixture.Categories.movies();
        final var expectedId = movies.id();

        doNothing()
                .when(this.categoryGateway).deleteById(anyString());

        // when
        Assertions.assertDoesNotThrow(() -> this.useCase.execute(expectedId));

        // then
        verify(this.categoryGateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    void givenInvalidId_whenCallsDelete_shouldBeOk() {
        // given
        final String expectedId = null;

        // when
        Assertions.assertDoesNotThrow(() -> this.useCase.execute(expectedId));

        // then
        verify(this.categoryGateway, never()).deleteById(eq(expectedId));
    }
}
