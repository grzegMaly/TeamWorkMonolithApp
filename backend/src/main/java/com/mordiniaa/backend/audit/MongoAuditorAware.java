package com.mordiniaa.backend.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("mongoAuditor")
public class MongoAuditorAware {

    @Bean
    public AuditorAware<String> getCurrentAuditor() {
        return () -> Optional.of("MONGO_TEST");
    }
}
