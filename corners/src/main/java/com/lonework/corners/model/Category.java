package com.lonework.corners.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Category {
    @Id
    private Long id;

    private String name;

    private boolean main;

    private String title;

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Place> places = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Trip> trips = new ArrayList<>();

    public Category() {
    }

    public Category(Long id, String name, String value, boolean main, String title, List<Place> places, List<Trip> trips) {
        this.id = id;
        this.name = name;
        this.main = main;
        this.title = title;
        this.places = places;
        this.trips = trips;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
}
