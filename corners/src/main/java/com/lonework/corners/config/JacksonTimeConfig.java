package com.lonework.corners.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class JacksonTimeConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void registerModules() {
        objectMapper.registerModule(new JavaTimeModule());
    }
}