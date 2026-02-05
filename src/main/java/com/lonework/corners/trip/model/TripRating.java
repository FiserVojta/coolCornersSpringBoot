package com.lonework.corners.trip.model;

import com.lonework.corners.trip.model.DTO.TripRateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
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

    public TripRating(TripRateRequest tripRateRequest, Long tripId) {
        this.tripId = tripId;
        this.author = tripRateRequest.createdBy();
        this.rating = tripRateRequest.rating();
        this.createdAt = ZonedDateTime.now();
    }
}
