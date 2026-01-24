package com.lonework.corners.trip.model;

import com.lonework.corners.trip.model.DTO.TripRateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;


@Entity
public class TripRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long tripId;

    @Column
    private String author;

    @Column
    private Integer rating;

    @Column
    private ZonedDateTime createdAt;

    public TripRating() {
    }

    public TripRating(TripRateRequest tripRateRequest, Long tripId) {
        this.tripId = tripId;
        this.author = tripRateRequest.createdBy();
        this.rating = tripRateRequest.rating();
        this.createdAt = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TripRating that)) {return false;}
        return Objects.equals(id, that.id) && Objects.equals(tripId, that.tripId) && Objects.equals(author, that.author) && Objects.equals(
                rating,
                that.rating) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tripId, author, rating, createdAt);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TripRating.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("tripId=" + tripId)
                .add("author='" + author + "'")
                .add("rating=" + rating)
                .add("createdAt=" + createdAt)
                .toString();
    }
}
