package com.mordiniaa.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoAuditing(auditorAwareRef = "mongoAuditor")
@EnableJpaRepositories(basePackages = "com.mordiniaa.backend.repositories.jpa")
@EnableMongoRepositories(basePackages = "com.mordiniaa.backend.repositories.mongo")
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
