package com.lonework.corners.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class JsonConfig {

    private static final int GEOMETRY_JSON_PRECISION = 7;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .registerModule(new GeoJsonModule())
                .getSerializerProvider()
                .setNullKeySerializer(new NullKeySerializer());
        mapper.registerModule(new JtsModule());
        return mapper;
    }

    public static class NullKeySerializer extends StdSerializer<Object> {
        protected NullKeySerializer() {
            super((Class<Object>) null);
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeFieldName("");
        }
    }

    public static class FeatureSerializer extends StdSerializer<SimpleFeature> {
        private final GeometryJSON geometryJson = new GeometryJSON(GEOMETRY_JSON_PRECISION);

        public FeatureSerializer() {
            super(SimpleFeature.class);
        }

        @Override
        public void serialize(SimpleFeature value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            FeatureJSON featureJSON = new FeatureJSON(geometryJson);
            StringWriter writer = new StringWriter();
            featureJSON.writeFeature(value, writer);
            jsonGenerator.writeRawValue(writer.toString());
        }
    }

    public static class GeometrySerializer extends StdSerializer<Geometry> {
        public GeometrySerializer() {
            super(Geometry.class);
        }

        @Override
        public void serialize(Geometry value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            GeometryJSON gjson = new GeometryJSON(GEOMETRY_JSON_PRECISION);
            StringWriter writer = new StringWriter();
            gjson.write(value, writer);
            jsonGenerator.writeRawValue(writer.toString());
        }
    }

    public static class PointSerializer extends StdSerializer<Point> {
        public PointSerializer() {
            super(Point.class);
        }

        @Override
        public void serialize(Point value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            GeometryJSON gjson = new GeometryJSON(GEOMETRY_JSON_PRECISION);
            StringWriter writer = new StringWriter();
            gjson.write(value, writer);
            jsonGenerator.writeRawValue(writer.toString());
        }
    }

    public static class ListFeatureCollectionSerializer extends StdSerializer<ListFeatureCollection> {
        public ListFeatureCollectionSerializer() {
            super(ListFeatureCollection.class);
        }

        @Override
        public void serialize(ListFeatureCollection featureCollection, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("type", "FeatureCollection");

            gen.writeArrayFieldStart("features");
            for (SimpleFeature feature : featureCollection) {
                gen.writeStartObject();
                gen.writeStringField("type", "Feature");

                gen.writeObjectFieldStart("properties");
                for (AttributeDescriptor descriptor : feature.getFeatureType().getAttributeDescriptors()) {
                    String name = descriptor.getLocalName();
                    if (feature.getAttribute(name) != null && !Objects.equals(name, "geometry")) {
                        Object value = feature.getAttribute(name);
                        gen.writeObjectField(name, value);
                    }
                }
                gen.writeEndObject();

                gen.writeObjectField("geometry", feature.getDefaultGeometry());
                gen.writeStringField("id", feature.getID());
                gen.writeEndObject();
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }

    public static class GeometryDeserializer extends JsonDeserializer<Geometry> {
        @Override
        public Geometry deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            GeometryJSON gjson = new GeometryJSON(GEOMETRY_JSON_PRECISION);
            return gjson.read(p.readValueAsTree().toString());
        }
    }

    public static class LineStringDeserializer extends JsonDeserializer<LineString> {
        @Override
        public LineString deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            GeometryJSON gjson = new GeometryJSON(GEOMETRY_JSON_PRECISION);
            Geometry geometry = gjson.read(p.readValueAsTree().toString());

            if (geometry instanceof LineString) {
                return (LineString) geometry;
            } else {
                throw new IOException("Expected LineString geometry, but got: " + geometry.getGeometryType());
            }
        }
    }

    public static class PointDeserializer extends JsonDeserializer<Point> {
        @Override
        public Point deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            GeometryJSON gjson = new GeometryJSON(GEOMETRY_JSON_PRECISION);
            Geometry geometry = gjson.read(parser.readValueAsTree().toString());

            if (geometry instanceof Point) {
                return (Point) geometry;
            } else {
                throw new IOException("Expected Point geometry, found: " + geometry.getGeometryType());
            }
        }
    }

    public static class FeatureCollectionDeserializer extends JsonDeserializer<SimpleFeatureCollection> {
        private final GeometryDeserializer geometryDeserializer = new GeometryDeserializer();

        @Override
        public SimpleFeatureCollection deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            Map<String, Object> rawJson = jsonParser.readValueAs(Map.class);
            DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

            List<Map<String, Object>> features = (List<Map<String, Object>>) rawJson.get("features");
            for (Map<String, Object> featureData : features) {
                SimpleFeature feature = buildFeatureFromJson(featureData);
                if (feature != null) {
                    featureCollection.add(feature);
                }
            }

            return featureCollection;
        }

        private SimpleFeature buildFeatureFromJson(Map<String, Object> featureData) {
            Map<String, Object> properties = (Map<String, Object>) featureData.get("properties");
            Map<String, Object> geometryData = (Map<String, Object>) featureData.get("geometry");

            SimpleFeatureType featureType = createDynamicFeatureType(properties, true);
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                featureBuilder.set(entry.getKey(), entry.getValue());
            }

            Geometry geometry = processGeometry(geometryData);
            featureBuilder.set("geometry", geometry);

            String featureId = (String) featureData.get("id");
            return featureBuilder.buildFeature(featureId);
        }

        private SimpleFeatureType createDynamicFeatureType(Map<String, Object> properties, boolean includeGeometry) {
            SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
            builder.setName("DynamicFeatureType");

            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof String) {
                    builder.add(key, String.class);
                } else if (value instanceof Integer) {
                    builder.add(key, Integer.class);
                } else if (value instanceof Double) {
                    builder.add(key, Double.class);
                } else if (value instanceof Boolean) {
                    builder.add(key, Boolean.class);
                } else if (value instanceof Long) {
                    builder.add(key, Long.class);
                } else {
                    builder.add(key, Object.class);
                }
            }

            if (includeGeometry) {
                builder.add("geometry", Geometry.class);
            }

            return builder.buildFeatureType();
        }

        private Geometry processGeometry(Map<String, Object> geometryData) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode geometryJson = mapper.convertValue(geometryData, ObjectNode.class);
                return geometryDeserializer.deserialize(geometryJson.traverse(mapper), null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class GeoJsonModule extends SimpleModule {
        public GeoJsonModule() {
            super("GeoJsonModule");
            addSerializer(SimpleFeature.class, new FeatureSerializer());
            addSerializer(Geometry.class, new GeometrySerializer());
            addDeserializer(Geometry.class, new GeometryDeserializer());
            addSerializer(Point.class, new PointSerializer());
            addDeserializer(Point.class, new PointDeserializer());
            addDeserializer(LineString.class, new LineStringDeserializer());
            addDeserializer(SimpleFeatureCollection.class, new FeatureCollectionDeserializer());
            addSerializer(ListFeatureCollection.class, new ListFeatureCollectionSerializer());
        }
    }
}