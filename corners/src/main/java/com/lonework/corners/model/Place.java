package com.lonework.corners.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Place {
    @Id
    private Long id;
    private String name;
    private String description;
    private double rating;
    private String phoneNumber;
    private double price;
    private String openingHours;

    private String image;

    @OneToOne(mappedBy = "place")
    @JsonManagedReference
    private CustomLocation location;
    private String city;
    private String gallery;

    @OneToMany(mappedBy = "place")
    @JsonManagedReference
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "place")
    @JsonManagedReference
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(mappedBy = "places")
    @JsonManagedReference
    private Set<Tag> tags = new HashSet<>();

    public Place() {
    }

    public Place(Long id, String name, String description, double rating, String phoneNumber, double price,
            String openingHours, String image, CustomLocation location, String city, String gallery,
            Set<Comment> comments, Set<Category> categories, Set<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.openingHours = openingHours;
        this.image = image;
        this.location = location;
        this.city = city;
        this.gallery = gallery;
        this.comments = comments;
        this.categories = categories;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

}
