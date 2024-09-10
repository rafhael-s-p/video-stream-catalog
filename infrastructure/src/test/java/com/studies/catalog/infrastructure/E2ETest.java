package com.studies.catalog.infrastructure;

import com.studies.catalog.infrastructure.configuration.WebServerConfig;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Tag("e2eTest")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("e2e-test")
@SpringBootTest(classes = WebServerConfig.class)
@AutoConfigureMockMvc
public @interface E2ETest {
}