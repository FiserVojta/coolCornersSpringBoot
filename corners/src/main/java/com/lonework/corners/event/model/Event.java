package com.lonework.corners.event.model;

import com.lonework.corners.event.model.DTO.EventCreateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;
import java.util.Objects;


@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String createdBy;

    @Column
    private ZonedDateTime createdAt;

    @Column
    private String venue;

    public Event() {
        this.createdAt = ZonedDateTime.now();
    }

    public Event(EventCreateRequest eventCreateRequest){
        this.name = eventCreateRequest.name();
        this.createdBy = eventCreateRequest.createdBy();
        this.venue = eventCreateRequest.location();
        this.description = eventCreateRequest.description();
        this.createdAt = ZonedDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event event)) {return false;}
        return Objects.equals(id, event.id) && Objects.equals(name, event.name) && Objects.equals(description, event.description)
                && Objects.equals(createdBy, event.createdBy) && Objects.equals(createdAt, event.createdAt) && Objects.equals(venue, event.venue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, createdBy, createdAt, venue);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
