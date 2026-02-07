package com.mordiniaa.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "mysqlAuditor")
public class JpaAuditorConfig {
}
