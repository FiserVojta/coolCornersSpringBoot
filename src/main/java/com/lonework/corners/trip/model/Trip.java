package com.lonework.corners.trip.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.category.model.Category;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.wander.model.WanderPart;
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
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;


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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Category category;

    private String image;

    private String creator;

    @Column(name = "created_by")
    private String createdBy;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "trip_has_tag",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Tag> tags = new ArrayList<>();

    @ManyToMany(mappedBy = "trips")
    @JsonBackReference
    private List<WanderPart> wanderparts = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "trip_has_place",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id"))
    @JsonManagedReference("trip-places")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Place> places;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "trip_has_google_place",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id"))
    @JsonManagedReference("trip-google-places")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<GooglePlace> googlePlaces;

    @OneToMany(mappedBy = "trip")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Comment> comments;

    private Geometry geometry;

    public Trip() {
    }

    public Trip(TripCreateRequest tripCreateRequest, String createdBy) {
        this.geometry = tripCreateRequest.getGeometry();
        this.name = tripCreateRequest.getName();
        this.duration = tripCreateRequest.getDuration();
        this.description = tripCreateRequest.getDescription();
        this.creator = tripCreateRequest.getAuthor();
        this.createdBy = createdBy;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String author) {
        this.creator = author;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<WanderPart> getWanderparts() {
        return wanderparts;
    }

    public void setWanderparts(List<WanderPart> wanderparts) {
        this.wanderparts = wanderparts;
    }

    public List<GooglePlace> getGooglePlaces() {
        return googlePlaces;
    }

    public void setGooglePlaces(List<GooglePlace> googlePlaces) {
        this.googlePlaces = googlePlaces;
    }
}
