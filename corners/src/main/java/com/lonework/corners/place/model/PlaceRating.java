package com.lonework.corners.place.model;

import com.lonework.corners.trip.model.TripRateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;


@Entity
public class PlaceRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long placeId;

    @Column
    private String author;

    @Column
    private Integer rating;

    @Column
    private ZonedDateTime createdAt;

    public PlaceRating() {
    }

    public PlaceRating(PlaceRateRequest placeRateRequest, Long placeId) {
        this.placeId = placeId;
        this.author = placeRateRequest.createdBy();
        this.rating = placeRateRequest.rating();
        this.createdAt = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
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
        if (!(o instanceof PlaceRating that)) {return false;}
        return Objects.equals(id, that.id) && Objects.equals(placeId, that.placeId) && Objects.equals(author, that.author) && Objects.equals(
                rating,
                that.rating) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, placeId, author, rating, createdAt);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PlaceRating.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("placeId=" + placeId)
                .add("author='" + author + "'")
                .add("rating=" + rating)
                .add("createdAt=" + createdAt)
                .toString();
    }
}
