package com.lonework.corners.services;

import com.lonework.corners.model.Category;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class FeatureTypeClass {

    public SimpleFeatureType placeFeatureType() {
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName("Place");
        typeBuilder.add("id", UUID.class);
        typeBuilder.add("name", String.class);
        typeBuilder.add("geometry", Geometry.class);
        return typeBuilder.buildFeatureType();
    }
}
