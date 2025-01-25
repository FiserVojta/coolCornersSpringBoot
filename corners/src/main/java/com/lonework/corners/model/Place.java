package com.lonework.corners.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.model.request.PlaceCreateRequest;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;


@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String phoneNumber;

    @Column
    private Double price;

    @Column
    private String openingHours;

    @Column
    private String image;

    @OneToMany(mappedBy = "place")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Category category;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "place_has_tag", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<Tag> tags = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "trip_has_place", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "trip_id"))
    @JsonBackReference
    private List<Trip> trips = new ArrayList<>();

    public Place() {
    }

    public Place(PlaceCreateRequest placeRequest) {
        this.name = placeRequest.getName();
        this.description = placeRequest.getDescription();
        this.openingHours = placeRequest.getOpeningHours();
        this.price = placeRequest.getPrice();
        this.image = placeRequest.getImage();
        this.phoneNumber = placeRequest.getPhoneNumber();
    }

    public Place(Long id, String name, String description, String phoneNumber, Double price,
            String openingHours, String image,
            List<Comment> comments, Category category, List<Tag> tags, Double longitude, Double latitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.openingHours = openingHours;
        this.image = image;
        this.comments = comments;
        this.category = category;
        this.tags = tags;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
}
