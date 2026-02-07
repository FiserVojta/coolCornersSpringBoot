package com.lonework.corners.wander.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.trip.model.Trip;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "wanderpart")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"places", "trips", "wander"})
public class WanderPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "wanderpart_place",
            joinColumns = @JoinColumn(name = "wanderpart_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id"))
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Place> places = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "wanderpart_trip",
            joinColumns = @JoinColumn(name = "wanderpart_id"),
            inverseJoinColumns = @JoinColumn(name = "trip_id"))
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Trip> trips = new ArrayList<>();

    @Column(name = "\"order\"")
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wander_part_id")
    @JsonManagedReference
    @JsonIgnore
    private Wander wander;

    @Column(name = "name")
    private String name;

}
