package com.lonework.corners.place.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lonework.corners.trip.model.Trip;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
public class GooglePlace {

    @Id
    private String placeId;

    private String name;

    @ManyToMany(mappedBy = "googlePlaces", cascade = CascadeType.PERSIST)
    @JsonBackReference("trip-google-places")
    private List<Trip> trips = new ArrayList<>();

    private Geometry geometry;

    public void setId(String placeId) {
        this.placeId = placeId;
    }

    public String getId() {
        return placeId;
    }
}
