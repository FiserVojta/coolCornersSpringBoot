package com.lonework.corners.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.model.request.TripCreateRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double rating;

    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Category category;

    private String image;

    private String author;

    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private CustomLocation location;

    private String gallery;

    @ManyToMany(mappedBy = "trips")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(mappedBy = "trips", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Set<Place> places = new HashSet<>();

    public Trip() {
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public void setLocation(CustomLocation location) {
        this.location = location;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Place> getPlaces() {
        return places;
    }

    public void setPlaces(Set<Place> places) {
        this.places = places;
    }

}
