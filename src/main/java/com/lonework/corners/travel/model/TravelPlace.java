package com.lonework.corners.travel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

/**
 * A single place a user visited on a travel — a named map point (lat/lng).
 */
@Entity
@Table(name = "travel_place")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "travel")
public class TravelPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_id")
    @JsonIgnore
    private Travel travel;

    @Column
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    /**
     * How the traveller got here from the previous place; null falls back to the travel's
     * own transport mode. Meaningless on the first place.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_mode")
    private TravelTransportMode transportMode;

    /**
     * Routed line of the leg arriving at this place, as [lat, lng] pairs, cached so the map does
     * not have to call the routing API on every view. Null when not cached.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "route_geometry", columnDefinition = "jsonb")
    private List<List<Double>> routeGeometry;
}
