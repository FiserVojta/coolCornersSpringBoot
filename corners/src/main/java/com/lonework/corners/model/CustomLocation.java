package com.lonework.corners.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.model.request.LocationCreateRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class CustomLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private String title;
    private String area;
    private String city;
    private String state;
    private Long zoom;

    @OneToMany(mappedBy = "location")
    @JsonBackReference
    private Set<Place> places = new HashSet<>();

    @OneToMany(mappedBy = "customLocation")
    @JsonBackReference
    private Set<City> cities = new HashSet<>();

    public CustomLocation() {
    }

    public CustomLocation(LocationCreateRequest locationReq) {
        this.latitude = locationReq.getLatitude();
        this.longitude = locationReq.getLongitude();

    }

    public CustomLocation(Long id, String name, double latitude, double longitude, String title, String area,
            String city, String state, Set<Place> places) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.area = area;
        this.city = city;
        this.state = state;
        this.places = places;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Set<Place> getPlaces() {
        return places;
    }

    public void setPlaces(Set<Place> places) {
        this.places = places;
    }

    public Long getZoom() {
        return zoom;
    }

    public void setZoom(Long zoom) {
        this.zoom = zoom;
    }

    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }

}
