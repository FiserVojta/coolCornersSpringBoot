package com.lonework.corners.wander.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.trip.model.Trip;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "wanderpart")
public class WanderPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "wanderparts", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private List<Place> places = new ArrayList<>();

    @ManyToMany(mappedBy = "wanderparts", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private List<Trip> trips = new ArrayList<>();

    @Column
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wander_part_id")
    @JsonManagedReference
    @JsonIgnore
    private Wander wander;

    public WanderPart() {
    }

    public WanderPart(WanderCreateRequest.WanderPartCreateRequest wanderPartCreateRequest) {
        this.places = new ArrayList<>();
        this.trips = new ArrayList<>();
        if (wanderPartCreateRequest.places() != null) {
            wanderPartCreateRequest.places().forEach(placeId -> {
                Place place = new Place();
                place.setId(placeId);
                this.places.add(place);
            });
        }
        if (wanderPartCreateRequest.trips() != null) {
            wanderPartCreateRequest.trips().forEach(tripId -> {
                Trip trip = new Trip();
                trip.setId(tripId);
                this.trips.add(trip);
            });
        }
        this.order = wanderPartCreateRequest.order();
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WanderPart that)) {return false;}
        return Objects.equals(id, that.id) && Objects.equals(places, that.places) && Objects.equals(trips, that.trips) && Objects.equals(order,
                that.order) && Objects.equals(wander, that.wander);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, places, trips, order, wander);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Wander getWander() {
        return wander;
    }

    public void setWander(Wander wander) {
        this.wander = wander;
    }
}
