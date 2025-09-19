package com.room_reservation.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("room_reservation")
            .withUsername("user1")
            .withPassword("1234")
            .withInitScript("init-test.sql");

    static {
        postgres.start();
    }

    @Bean
    PostgreSQLContainer<?> postgresContainer() {
        return postgres;
    }
}
