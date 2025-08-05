package com.lonework.corners.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lonework.corners.wander.model.Wander;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "corneruser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", unique = true, nullable = false)
    private String keycloakId;

    @Column(nullable = false)
    private String email;

    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "created_at")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @ManyToMany(mappedBy = "wanderers", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private List<Wander> wanders = new ArrayList<>();


    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private List<Wander> wandersOrganized = new ArrayList<>();

    public User() {
    }


    public User(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Wander> getWanders() {
        return wanders;
    }

    public void setWanders(List<Wander> wanders) {
        this.wanders = wanders;
    }

    public List<Wander> getWandersOrganized() {
        return wandersOrganized;
    }

    public void setWandersOrganized(List<Wander> wandersOrganized) {
        this.wandersOrganized = wandersOrganized;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) {return false;}
        return Objects.equals(id, user.id) && Objects.equals(keycloakId, user.keycloakId) && Objects.equals(email, user.email)
                && Objects.equals(name, user.name) && Objects.equals(displayName, user.displayName) && Objects.equals(createdAt, user.createdAt)
                && Objects.equals(wanders, user.wanders) && Objects.equals(wandersOrganized, user.wandersOrganized);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keycloakId, email, name, displayName, createdAt, wanders, wandersOrganized);
    }
}