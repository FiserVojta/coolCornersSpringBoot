package com.lonework.corners.travel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;


public record TravelDayNoteRequest(
        @JsonProperty LocalDate day,
        @JsonProperty String note
) {
}
