package com.lonework.corners.event.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.category.model.Category;
import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.event.model.DTO.EventCreateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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

    @Column
    private Integer capacity;

    @Column
    private Integer duration;

    @Column
    private ZonedDateTime startTime;

    @Column
    private Double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Category category;

    @Column
    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus;

    public Event() {
        this.createdAt = ZonedDateTime.now();
    }

    public Event(EventCreateRequest eventCreateRequest, Category category, String createdBy){
        this.name = eventCreateRequest.name();
        this.createdBy = createdBy;
        this.venue = eventCreateRequest.venue();
        this.description = eventCreateRequest.description();
        this.createdAt = ZonedDateTime.now();
        this.capacity = eventCreateRequest.capacity();
        this.startTime = eventCreateRequest.startTime();
        this.duration = eventCreateRequest.duration();
        this.price = eventCreateRequest.price();
        this.category = category;
        this.entityStatus = EntityStatus.ACTIVE;
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }
}
