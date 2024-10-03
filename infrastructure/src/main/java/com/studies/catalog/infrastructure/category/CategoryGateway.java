package com.studies.catalog.infrastructure.category;

import com.studies.catalog.domain.category.Category;

import java.util.Optional;

public interface CategoryGateway {

    Optional<Category> categoryOfId(String anId);
}
