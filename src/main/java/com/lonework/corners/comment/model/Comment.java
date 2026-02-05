package com.lonework.corners.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.trip.model.DTO.TripCommentRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String value;

    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @JsonManagedReference
    @JsonIgnore
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    @JsonManagedReference
    @JsonIgnore
    private Trip trip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private Double rating;

    public Comment(TripCommentRequest tripCommentRequest, Trip trip, String createdBy) {
        this.name = createdBy;
        this.value = tripCommentRequest.value();
        this.author = createdBy;
        this.created = LocalDateTime.now();
        this.rating = null;
        this.trip = trip;
    }
}
