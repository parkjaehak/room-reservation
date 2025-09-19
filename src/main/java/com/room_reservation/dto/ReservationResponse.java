package com.room_reservation.dto;

import java.time.OffsetDateTime;

public record ReservationResponse(
        Long id,
        Long roomId,
        String roomName,
        String roomLocation,
        Integer roomCapacity,
        Long userId,
        OffsetDateTime startAt,
        OffsetDateTime endAt
) {
}
