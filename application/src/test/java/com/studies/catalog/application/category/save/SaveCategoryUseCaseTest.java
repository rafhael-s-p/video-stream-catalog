package com.studies.catalog.application.category.save;

import com.studies.catalog.application.UseCaseTest;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.domain.category.Category;
import com.studies.catalog.domain.category.CategoryGateway;
import com.studies.catalog.domain.exceptions.DomainException;
import com.studies.catalog.domain.utils.InstantUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaveCategoryUseCaseTest extends UseCaseTest {

    @InjectMocks
    private SaveCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    void givenValidCategory_whenCallsSave_shouldPersistIt() {
        // given
        final var aCategory = Fixture.Categories.movies();

        when(categoryGateway.save(any()))
                .thenAnswer(returnsFirstArg());

        // when
        this.useCase.execute(aCategory);

        // then
        verify(categoryGateway, times(1)).save(eq(aCategory));
    }

    @Test
    void givenInvalidId_whenCallsSave_shouldReturnError() {
        // given
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        final var aCategory = Category.with(
                "",
                "Movies",
                "Some description",
                true,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        // when
        final var currentError = assertThrows(DomainException.class, () -> this.useCase.execute(aCategory));

        // then
        assertEquals(expectedErrorCount, currentError.getErrors().size());
        assertEquals(expectedErrorMessage, currentError.getErrors().get(0).message());

        verify(categoryGateway, times(0)).save(eq(aCategory));
    }

    @Test
    void givenInvalidName_whenCallsSave_shouldReturnError() {
        // given
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var aCategory = Category.with(
                UUID.randomUUID().toString().replace("-", ""),
                "",
                "Some description",
                true,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        // when
        final var currentError = assertThrows(DomainException.class, () -> this.useCase.execute(aCategory));

        // then
        assertEquals(expectedErrorCount, currentError.getErrors().size());
        assertEquals(expectedErrorMessage, currentError.getErrors().get(0).message());

        verify(categoryGateway, times(0)).save(eq(aCategory));
    }
}
