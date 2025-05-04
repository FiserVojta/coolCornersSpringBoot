package com.lonework.corners.category.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.trip.model.Trip;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Category {
    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private boolean main;

    @Column
    private String title;

    @Enumerated(EnumType.STRING)
    @Column
    private CategoryType categoryType;

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Place> places = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Trip> trips = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Event> events = new ArrayList<>();

    public Category() {
    }

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

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType type) {
        this.categoryType = type;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
