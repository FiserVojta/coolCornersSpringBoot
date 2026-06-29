package com.lonework.corners.travel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lonework.corners.files.model.CornerFile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A photo attached to a travel, with the optional location where it was taken
 * (from EXIF GPS or placed manually on the map).
 */
@Entity
@Table(name = "travel_photo")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"travel", "file"})
public class TravelPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_id")
    @JsonIgnore
    private Travel travel;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "file_id")
    private CornerFile file;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    /** Date the photo was taken (from EXIF or set manually). Used to group photos by day. */
    @Column(name = "taken_on")
    private LocalDate takenOn;
}
