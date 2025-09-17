package com.room_reservation.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record AvailabilityDto(
        Long roomId,
        String roomName,
        String location,
        Integer capacity,
        List<TimeRange> reservations,
        List<TimeRange> freeSlots
) {
    public record TimeRange(OffsetDateTime startAt, OffsetDateTime endAt) {}
}


