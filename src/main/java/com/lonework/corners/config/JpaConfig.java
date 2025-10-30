package com.lonework.corners.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.lonework.corners")
public class JpaConfig {
    // configuration methods
}