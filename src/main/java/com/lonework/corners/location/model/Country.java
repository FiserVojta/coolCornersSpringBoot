package com.lonework.corners.location.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Country {

    @Id
    private Long id;

    private String name;

    private String code;

    @OneToMany(mappedBy = "country")
    @JsonBackReference
    private List<City> cities = new ArrayList<>();
}
