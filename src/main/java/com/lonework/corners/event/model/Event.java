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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "description", "createdBy", "createdAt", "venue"})
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
}
