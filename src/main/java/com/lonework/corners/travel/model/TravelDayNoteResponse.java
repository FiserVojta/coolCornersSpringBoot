package com.lonework.corners.travel.model;

import java.time.LocalDate;


public record TravelDayNoteResponse(
        Long id,
        LocalDate day,
        String note
) {
    public static TravelDayNoteResponse from(TravelDayNote dayNote) {
        return new TravelDayNoteResponse(
                dayNote.getId(),
                dayNote.getDay(),
                dayNote.getNote()
        );
    }
}
