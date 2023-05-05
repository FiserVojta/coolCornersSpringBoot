package com.lonework.corners.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class State {

    @Id
    private Long id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "state")
    @JsonBackReference
    private Set<City> cities = new HashSet<>();

    @OneToMany(mappedBy = "state")
    @JsonBackReference
    private Set<Place> places = new HashSet<>();

    public State() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
