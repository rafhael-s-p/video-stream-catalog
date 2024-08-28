package com.studies.catalog.infrastructure;

import com.studies.catalog.infrastructure.configuration.WebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideoStreamCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebServerConfig.class, args);
    }

}