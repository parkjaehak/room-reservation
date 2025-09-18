package com.room_reservation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record CreateReservationRequest(
        @NotNull Long roomId,
        @NotNull OffsetDateTime startAt,
        @NotNull OffsetDateTime endAt) {

}


