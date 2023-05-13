package com.lonework.corners.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lonework.corners.model.request.CommentCreateRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String value;
    private String title;
    private String author;
    private Date created;
    private double rating;
    @ManyToOne
    @JoinColumn(name = "place_id")
    @JsonBackReference
    private Place place;

    public Comment() {
    }

    public Comment(CommentCreateRequest request) {
        this.name = request.getName();
        this.title = request.getTitle();
        this.value = request.getValue();
        this.author = request.getAuthor();
    }

    public Comment(Long id, String name, String value, String title, String author, Date created, double rating,
            Place place) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.title = title;
        this.author = author;
        this.created = created;
        this.rating = rating;
        this.place = place;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

}
