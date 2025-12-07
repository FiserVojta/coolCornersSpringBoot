package com.lonework.corners.wander.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lonework.corners.category.model.Category;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "wander")
public class Wander {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private User createdBy;

    @Column
    private String description;

    @Column
    private Integer capacity;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "wander_has_wanderers",
            joinColumns = @JoinColumn(name = "wander_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<User> wanderers;


    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "wander_has_tag",
            joinColumns = @JoinColumn(name = "wander_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<Tag> tags = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Category category;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "wander_has_parts",
            joinColumns = @JoinColumn(name = "wander_id"),
            inverseJoinColumns = @JoinColumn(name = "wander_part_id"))
    @JsonManagedReference
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<WanderPart> wanderParts;


    public Wander() {
    }

    public Wander(WanderCreateRequest wanderCreateRequest, String createdBy) {
        this.createdBy = new User();
        this.description = wanderCreateRequest.description();
        this.capacity = wanderCreateRequest.capacity();
        this.startTime = wanderCreateRequest.startTime();

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Wander wander)) {return false;}
        return Objects.equals(id, wander.id) && Objects.equals(createdBy, wander.createdBy) && Objects.equals(description, wander.description)
                && Objects.equals(capacity, wander.capacity) && Objects.equals(startTime, wander.startTime) && Objects.equals(wanderers, wander.wanderers)
                && Objects.equals(tags, wander.tags) && Objects.equals(category, wander.category) && Objects.equals(wanderParts, wander.wanderParts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdBy, description, capacity, startTime, wanderers, tags, category, wanderParts);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public List<User> getWanderers() {
        return wanderers;
    }

    public void setWanderers(List<User> wanderers) {
        this.wanderers = wanderers;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<WanderPart> getWanderParts() {
        return wanderParts;
    }

    public void setWanderParts(List<WanderPart> wanderPart) {
        this.wanderParts = wanderPart;
    }
}
