package com.lonework.corners.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lonework.corners.wander.model.Wander;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "corneruser")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"wanders", "wandersOrganized", "friends", "friendOf", "followers", "followersOf", "completedTrips"})
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

    @ManyToMany
    @JoinTable(
            name = "corneruser_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();


    @ManyToMany(mappedBy = "friends")
    @JsonBackReference
    private Set<User> friendOf = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "corneruser_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers = new HashSet<>();


    @ManyToMany(mappedBy = "followers")
    @JsonBackReference
    private Set<User> followersOf = new HashSet<>();

    @ManyToMany(mappedBy = "completedByUsers")
    @JsonBackReference("user-completed-trips")
    private List<com.lonework.corners.trip.model.Trip> completedTrips = new ArrayList<>();

    public User(Long id) {
        this.id = id;
    }

    public void addFriend(User p) {
        friends.add(p);
        p.getFriendOf().add(this);
    }

    public void addFollower(User user) {
        followers.add(user);
        user.getFriendOf().add(this);
    }

    public void removeFollower(User user) {
        followers.remove(user);
        user.getFriendOf().remove(this);
    }
}
