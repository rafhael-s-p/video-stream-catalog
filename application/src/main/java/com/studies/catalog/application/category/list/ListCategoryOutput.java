package com.studies.catalog.application.category.list;

import com.studies.catalog.domain.category.Category;

public record ListCategoryOutput(
        String id,
        String name
) {

    public static ListCategoryOutput from(final Category aCategory) {
        return new ListCategoryOutput(aCategory.id(), aCategory.name());
    }
}
