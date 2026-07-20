package com.lonework.corners.travel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;


/**
 * A single user's rating of a travel. Unlike {@code TripRating}, there is at most
 * one row per (travel, author) — re-rating updates the existing row.
 */
@Entity
@Table(name = "travel_rating")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TravelRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "travel_id", nullable = false)
    private Long travelId;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;
}
