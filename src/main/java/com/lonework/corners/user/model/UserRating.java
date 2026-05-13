package com.lonework.corners.user.model;

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
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column
    private String author;

    @Column
    private Integer rating;

    @Column
    private ZonedDateTime createdAt;

    public UserRating(UserRateRequest request, Long userId) {
        this.userId = userId;
        this.author = request.createdBy();
        this.rating = request.rating();
        this.createdAt = ZonedDateTime.now();
    }
}
