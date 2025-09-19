package com.room_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;


public record CreateReservationRequest(
        @NotNull
        @Schema(description = "예약할 회의실 ID", example = "1")
        Long roomId,

        @NotNull
        @Schema(description = "예약 시작 시각", example = "2025-09-19T09:00:00+09:00")
        OffsetDateTime startAt,

        @NotNull
        @Schema(description = "예약 종료 시각", example = "2025-09-19T11:00:00+09:00")
        OffsetDateTime endAt
) {}
