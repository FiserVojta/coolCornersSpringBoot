package com.lonework.corners.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
        // Serialize java.time values as ISO-8601 strings (e.g. "2026-01-12") instead of numeric
        // arrays, which is what the frontend (new Date(...)) expects for all date fields.
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}