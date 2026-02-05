package com.lonework.corners.place.model;

import com.lonework.corners.place.model.DTO.PlaceRateRequest;
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

    public PlaceRating(PlaceRateRequest placeRateRequest, Long placeId) {
        this.placeId = placeId;
        this.author = placeRateRequest.createdBy();
        this.rating = placeRateRequest.rating();
        this.createdAt = ZonedDateTime.now();
    }
}
