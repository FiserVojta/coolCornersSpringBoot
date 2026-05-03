package com.lonework.corners.place.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.category.model.Category;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.place.model.DTO.PlaceCreateRequest;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.trip.model.Trip;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.locationtech.jts.geom.Geometry;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"comments", "tags", "wanderparts", "trips"})
@ToString(exclude = {"comments", "tags", "wanderparts", "trips"})
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

    @Column
    private String createdBy;

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

    @ManyToMany(mappedBy = "places")
    @JsonBackReference
    private List<WanderPart> wanderparts = new ArrayList<>();


    @ManyToMany(mappedBy = "places", cascade = CascadeType.PERSIST)
    @JsonBackReference("trip-places")
    private List<Trip> trips = new ArrayList<>();

    @Column(columnDefinition = "geometry(Point, 4326)")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.GEOMETRY)
    private Geometry geometry;

    public Place(PlaceCreateRequest placeRequest, String createdBy) {
        this.name = placeRequest.name();
        this.description = placeRequest.description();
        this.openingHours = placeRequest.openingHours();
        this.price = placeRequest.price();
        this.image = placeRequest.image();
        this.phoneNumber = placeRequest.phoneNumber();
        this.geometry = placeRequest.geometry();
        this.createdBy = createdBy;
    }
}
