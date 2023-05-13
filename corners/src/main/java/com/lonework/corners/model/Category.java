package com.lonework.corners.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Category {
    @Id
    private Long id;
    private String name;
    private String value;
    private boolean main;
    private String title;

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private Set<Place> places = new HashSet<>();

    public Category() {
    }

    public Category(Long id, String name, String value, boolean main, String title, Set<Place> places) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.main = main;
        this.title = title;
        this.places = places;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Set<Place> getPlaces() {
        return places;
    }

    public void setPlaces(Set<Place> places) {
        this.places = places;
    }

}
