package com.lonework.corners.category.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.wander.model.Wander;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private boolean main;

    @Column
    private String title;

    @Enumerated(EnumType.STRING)
    @Column
    private CategoryType categoryType;

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Place> places = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Trip> trips = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Wander> wanders = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Event> events = new ArrayList<>();

    public Category(Long id) {
        this.id = id;
    }
}
