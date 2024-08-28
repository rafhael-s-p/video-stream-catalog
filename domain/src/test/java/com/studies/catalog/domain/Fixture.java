package com.studies.catalog.domain;

import com.studies.catalog.domain.category.Category;
import com.studies.catalog.domain.utils.InstantUtils;
import net.datafaker.Faker;

import java.util.UUID;

public final class Fixture {

    private static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static String description255() {
        return FAKER.lorem().fixedString(255);
    }

    public static String description258() {
        return FAKER.lorem().fixedString(258);
    }

    public static String description4000() {
        return FAKER.lorem().fixedString(4000);
    }

    public static String description4003() {
        return FAKER.lorem().fixedString(4003);
    }

    public static Integer year() {
        return FAKER.random().nextInt(2020, 2030);
    }

    public static Double duration() {
        return FAKER.options().option(175.0, 202.5, 35.5, 10.0, 2.0);
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static String title() {
        return FAKER.options().option(
                "The Godfather",
                "The Godfather Part II",
                "The Dark Knight"
        );
    }

    public static String checksum() {
        return "03fe62de";
    }

    public static final class Categories {

        public static Category movies() {
            return Category.with(
                    UUID.randomUUID().toString().replace("-", ""),
                    "Movies",
                    "Some description",
                    true,
                    InstantUtils.now(),
                    InstantUtils.now(),
                    null
            );
        }

        public static Category trailers() {
            return Category.with(
                    UUID.randomUUID().toString().replace("-", ""),
                    "Lives",
                    "Some description",
                    true,
                    InstantUtils.now(),
                    InstantUtils.now(),
                    null
            );
        }
    }

}