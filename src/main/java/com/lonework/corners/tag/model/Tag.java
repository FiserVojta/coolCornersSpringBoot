package com.lonework.corners.tag.model;

import java.util.HashSet;
import java.util.Set;

import com.lonework.corners.place.model.Place;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.wander.model.Wander;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
    private String creator;

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Set<Place> places = new HashSet<>();

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Set<Trip> trips = new HashSet<>();

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Set<Wander> wanders = new HashSet<>();

    public Tag(TagCreateRequest tagCreateRequest) {
        this.name = tagCreateRequest.getName();
        this.creator = tagCreateRequest.getCreator();
    }

    public Tag(Long id) {
        this.id = id;
    }

    public Tag(Long id, String name, String title, String value, String creator, Set<Place> places) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.places = places;
    }
}
