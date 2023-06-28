package com.lonework.corners.model;

import java.util.HashSet;
import java.util.Set;

import org.aspectj.weaver.tools.Trace;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lonework.corners.model.request.TagCreateRequest;
import com.lonework.corners.model.request.TagSearchRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String title;
    private String value;
    private String creator;

    @ManyToMany
    @JoinTable(name = "place_has_tag", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "place_id"))
    @JsonBackReference
    private Set<Place> places = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "trip_has_tag", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "trip_id"))
    @JsonBackReference
    private Set<Trip> trips = new HashSet<>();

    public Tag() {
    }

    public Tag(TagCreateRequest tagCreateRequest) {
        this.name = tagCreateRequest.getName();
        this.title = "";
        this.value = tagCreateRequest.getValue();
        this.creator = tagCreateRequest.getCreator();

    }

    public Tag(Long id, String name, String title, String value, String creator, Set<Place> places) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.value = value;
        this.creator = creator;
        this.places = places;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Set<Place> getPlaces() {
        return places;
    }

    public void setPlaces(Set<Place> places) {
        this.places = places;
    };

}
