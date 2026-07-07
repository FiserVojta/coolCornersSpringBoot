package com.lonework.corners.files.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.trip.model.Trip;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cornerfile")
@Getter
@Setter
@EqualsAndHashCode(exclude = "trips")
@ToString(exclude = "trips")
public class CornerFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String url;

    /** Public URL of the generated thumbnail; null for non-images or if generation failed. */
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    /** Storage key of the generated thumbnail; null when there is no thumbnail. */
    @Column(name = "thumbnail_name")
    private String thumbnailName;

    @Column
    private String createdBy;

    @Column
    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus;

    @Column
    private ZonedDateTime createdAt;

    @ManyToMany(mappedBy = "cornerFiles", cascade = CascadeType.PERSIST)
    @JsonBackReference("trip-corner-files")
    private List<Trip> trips = new ArrayList<>();
}
