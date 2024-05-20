package com.lonework.corners.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SimpleFeatureConfig {

    @Bean
    public SimpleModule simpleFeatureModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(SimpleFeature.class, new SimpleFeatureDeserializer());
        return module;
    }
}
