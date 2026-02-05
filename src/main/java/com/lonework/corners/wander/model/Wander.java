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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "wander")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"wanderers", "tags", "wanderParts"})
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

    public Wander(WanderCreateRequest wanderCreateRequest, String createdBy) {
        this.createdBy = new User();
        this.description = wanderCreateRequest.description();
        this.capacity = wanderCreateRequest.capacity();
        this.startTime = wanderCreateRequest.startTime();
    }
}
