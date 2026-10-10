package com.lonework.corners.tag.model;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import com.lonework.corners.place.model.Place;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.wander.model.Wander;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "normalized_name", nullable = false, length = 50)
    private String normalizedName;
    private String creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private TagStatus status = TagStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Set<Place> places = new HashSet<>();

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Set<Trip> trips = new HashSet<>();

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Set<Wander> wanders = new HashSet<>();

    public Tag(Long id) {
        this.id = id;
    }

    public Tag(Long id, String name, String title, String value, String creator, Set<Place> places) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.places = places;
    }

    @PrePersist
    void onCreate() {
        if (this.status == null) {
            this.status = TagStatus.ACTIVE;
        }
        if (this.createdAt == null) {
            this.createdAt = ZonedDateTime.now();
        }
        syncNormalizedName();
    }

    @PreUpdate
    void syncNormalizedName() {
        this.normalizedName = TagNames.normalize(this.name);
    }
}
