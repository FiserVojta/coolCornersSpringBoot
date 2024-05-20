package com.lonework.corners.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.io.StringReader;

public class SimpleFeatureDeserializer  extends JsonDeserializer<SimpleFeature> {


    @Override
    public SimpleFeature deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        FeatureJSON fjson = new FeatureJSON();
        return fjson.readFeature(new StringReader(jsonParser.readValueAsTree().toString()));

    }
}
