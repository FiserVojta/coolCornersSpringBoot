package com.lonework.corners.trip.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.category.model.Category;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.trip.model.DTO.TripCreateRequest;
import com.lonework.corners.user.model.User;
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
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "trip_has_corner_file",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    @JsonManagedReference("trip-corner-files")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<CornerFile> cornerFiles;

    @OneToMany(mappedBy = "trip")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Comment> comments;

    private Geometry geometry;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "background_image_id")
    private CornerFile backgroundImage;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "corneruser_completed_trip",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonManagedReference("user-completed-trips")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<User> completedByUsers = new ArrayList<>();

    public Trip(TripCreateRequest tripCreateRequest, String createdBy) {
        this.geometry = tripCreateRequest.geometry();
        this.name = tripCreateRequest.name();
        this.duration = tripCreateRequest.duration();
        this.description = tripCreateRequest.description();
        this.creator = tripCreateRequest.author();
        this.createdBy = createdBy;
    }
}
