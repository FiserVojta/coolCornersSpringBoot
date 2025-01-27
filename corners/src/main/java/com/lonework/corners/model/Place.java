package com.lonework.corners.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Geometry;


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

    @Column(columnDefinition = "geometry(Point, 4326)")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.GEOMETRY)
    private Geometry geometry;

    public Place() {
    }

    public Place(PlaceCreateRequest placeRequest) {
        this.name = placeRequest.getName();
        this.description = placeRequest.getDescription();
        this.openingHours = placeRequest.getOpeningHours();
        this.price = placeRequest.getPrice();
        this.image = placeRequest.getImage();
        this.phoneNumber = placeRequest.getPhoneNumber();
        this.geometry = placeRequest.getGeometry();
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

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof Place place)) {return false;}
        return Objects.equals(id, place.id) && Objects.equals(name, place.name) && Objects.equals(description, place.description)
                && Objects.equals(phoneNumber, place.phoneNumber) && Objects.equals(price, place.price) && Objects.equals(openingHours,
                place.openingHours) && Objects.equals(image, place.image) && Objects.equals(comments, place.comments) && Objects.equals(category,
                place.category) && Objects.equals(tags, place.tags) && Objects.equals(trips, place.trips) && Objects.equals(geometry, place.geometry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, phoneNumber, price, openingHours, image, comments, category, tags, trips, geometry);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Place.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("phoneNumber='" + phoneNumber + "'")
                .add("price=" + price)
                .add("openingHours='" + openingHours + "'")
                .add("image='" + image + "'")
                .add("comments=" + comments)
                .add("category=" + category)
                .add("tags=" + tags)
                .add("trips=" + trips)
                .add("geometry=" + geometry)
                .toString();
    }
}
