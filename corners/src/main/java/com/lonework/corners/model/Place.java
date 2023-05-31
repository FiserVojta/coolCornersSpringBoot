package com.lonework.corners.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.model.request.PlaceCreateRequest;
import com.lonework.corners.model.request.PlaceSearchRequest;
import com.lonework.corners.services.CategoryService;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Nullable
    private Double rating;
    private String phoneNumber;

    private Double price;
    private String openingHours;

    private String image;

    @ManyToOne
    @JoinColumn(name = "custom_location_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private CustomLocation location;
    private String gallery;

    @ManyToOne
    @JoinColumn(name = "state_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private State state;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "city_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private City city;

    @OneToMany(mappedBy = "place")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Category category;

    @ManyToMany(mappedBy = "places")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Set<Tag> tags = new HashSet<>();

    public Place() {
    }

    public Place(PlaceCreateRequest placeRequest) {
        this.name = placeRequest.getName();
        this.description = placeRequest.getDescription();
        this.gallery = placeRequest.getGallery();
        this.openingHours = placeRequest.getOpeningHours();
        this.price = placeRequest.getPrice();
        this.image = placeRequest.getImage();
        this.rating = placeRequest.getRating();
        this.phoneNumber = placeRequest.getPhoneNumber();

    }

    public Place(Long id, String name, String description, Double rating, String phoneNumber, Double price,
            String openingHours, String image, CustomLocation location, String gallery, State state, City city,
            Set<Comment> comments, Category category, Set<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.openingHours = openingHours;
        this.image = image;
        this.location = location;
        this.gallery = gallery;
        this.state = state;
        this.city = city;
        this.comments = comments;
        this.category = category;
        this.tags = tags;
    }

    public Place(Long id) {

        this.id = id;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

}
