package com.lonework.corners.model;

import com.lonework.corners.model.request.PlaceListRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trip_has_place")
public class TripHasPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "placeOrder")
    private Integer placeOrder;

    public TripHasPlace() {
    }

    public TripHasPlace(Long placeId, Long tripId) {
        this.placeId = placeId;
        this.tripId = tripId;

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

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Integer getPlaceOrder() {
        return placeOrder;
    }

    public void setPlaceOrder(Integer placeOrder) {
        this.placeOrder = placeOrder;
    }

    // Constructors, getters, setters
}