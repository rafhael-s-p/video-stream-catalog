package com.studies.catalog.infrastructure.category;

import com.studies.catalog.domain.category.Category;
import com.studies.catalog.domain.utils.InstantUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NoOpCategoryGateway implements CategoryGateway {

    @Override
    public Optional<Category> categoryOfId(final String anId) {
        return Optional.of(Category.with(
                anId,
                "Movies",
                null,
                true,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        ));
    }
}
