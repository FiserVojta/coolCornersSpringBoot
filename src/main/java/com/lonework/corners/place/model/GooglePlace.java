package com.lonework.corners.place.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lonework.corners.category.model.Category;
import com.lonework.corners.trip.model.Trip;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

    /** Category guessed from the Mapy label and editable by the trip creator; drives the map pin glyph. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "places", "trips", "wanders", "events" })
    private Category category;

    private Geometry geometry;

    public void setId(String placeId) {
        this.placeId = placeId;
    }

    public String getId() {
        return placeId;
    }
}
