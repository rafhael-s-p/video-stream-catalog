package com.studies.catalog;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Tag("integrationTest")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("integration-test")
@GraphQlTest
public @interface GraphQLControllerTest {

    @AliasFor(annotation = GraphQlTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};
}

