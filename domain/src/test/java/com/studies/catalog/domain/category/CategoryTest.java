package com.studies.catalog.domain.category;

import com.studies.catalog.domain.UnitTest;
import com.studies.catalog.domain.exceptions.DomainException;
import com.studies.catalog.domain.utils.InstantUtils;
import com.studies.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CategoryTest extends UnitTest {

    @Test
    void givenValidParams_whenCallsWith_thenInstantiateACategory() {
        // given
        final var expectedID = UUID.randomUUID().toString();
        final var expectedName = "Movies";
        final var expectedDescription = "Some description";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        // when
        final var currentCategory =
                Category.with(expectedID, expectedName, expectedDescription, expectedIsActive, expectedDates, expectedDates, expectedDates);

        // then
        Assertions.assertNotNull(currentCategory);
        Assertions.assertEquals(expectedID, currentCategory.id());
        Assertions.assertEquals(expectedName, currentCategory.name());
        Assertions.assertEquals(expectedDescription, currentCategory.description());
        Assertions.assertEquals(expectedIsActive, currentCategory.active());
        Assertions.assertEquals(expectedDates, currentCategory.createdAt());
        Assertions.assertEquals(expectedDates, currentCategory.updatedAt());
        Assertions.assertEquals(expectedDates, currentCategory.deletedAt());
    }

    @Test
    void givenValidParams_whenCallsWithCategory_thenInstantiateACategory() {
        // given
        final var expectedID = UUID.randomUUID().toString();
        final var expectedName = "Movies";
        final var expectedDescription = "Some description";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();

        final var aCategory =
                Category.with(expectedID, expectedName, expectedDescription, expectedIsActive, expectedDates, expectedDates, expectedDates);

        // then
        final var currentCategory = Category.with(aCategory);

        // when
        Assertions.assertNotNull(currentCategory);
        Assertions.assertEquals(aCategory.id(), currentCategory.id());
        Assertions.assertEquals(aCategory.name(), currentCategory.name());
        Assertions.assertEquals(aCategory.description(), currentCategory.description());
        Assertions.assertEquals(aCategory.active(), currentCategory.active());
        Assertions.assertEquals(aCategory.createdAt(), currentCategory.createdAt());
        Assertions.assertEquals(aCategory.updatedAt(), currentCategory.updatedAt());
        Assertions.assertEquals(aCategory.deletedAt(), currentCategory.deletedAt());
    }

    @Test
    void givenAnInvalidNullName_whenCallsNewCategoryAndValidate_thenShouldReceiveError() {
        // given
        final String expectedName = null;
        final var expectedID = UUID.randomUUID().toString();
        final var expectedDescription = "Some description";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var currentCategory =
                Category.with(expectedID, expectedName, expectedDescription, expectedIsActive, expectedDates, expectedDates, expectedDates);

        final var currentException =
                Assertions.assertThrows(DomainException.class, () -> currentCategory.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, currentException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, currentException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyName_whenCallsNewCategoryAndValidate_thenShouldReceiveError() {
        // given
        final String expectedName = " ";
        final var expectedID = UUID.randomUUID().toString();
        final var expectedDescription = "Some description";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        // when
        final var currentCategory =
                Category.with(expectedID, expectedName, expectedDescription, expectedIsActive, expectedDates, expectedDates, expectedDates);

        final var currentException =
                Assertions.assertThrows(DomainException.class, () -> currentCategory.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, currentException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, currentException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullId_whenCallsNewCategoryAndValidate_thenShouldReceiveError() {
        // given
        final String expectedID = null;
        final var expectedName = "Movies";
        final var expectedDescription = "Some description";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        // when
        final var currentCategory =
                Category.with(expectedID, expectedName, expectedDescription, expectedIsActive, expectedDates, expectedDates, expectedDates);

        final var currentException =
                Assertions.assertThrows(DomainException.class, () -> currentCategory.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, currentException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, currentException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyId_whenCallsNewCategoryAndValidate_thenShouldReceiveError() {
        // given
        final var expectedID = " ";
        final var expectedName = "Movies";
        final var expectedDescription = "Some description";
        final var expectedIsActive = true;
        final var expectedDates = InstantUtils.now();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'id' should not be empty";

        // when
        final var currentCategory =
                Category.with(expectedID, expectedName, expectedDescription, expectedIsActive, expectedDates, expectedDates, expectedDates);

        final var currentException =
                Assertions.assertThrows(DomainException.class, () -> currentCategory.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, currentException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, currentException.getErrors().get(0).message());
    }
}
