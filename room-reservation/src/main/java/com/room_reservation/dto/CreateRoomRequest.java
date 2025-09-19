package com.room_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateRoomRequest(
        @NotBlank
        @Schema(description = "회의실 이름", example = "회의실1")
        String name,

        @NotBlank
        @Schema(description = "회의실 위치", example = "서울시 강남구 빌딩 3층 A동")
        String location,

        @Min(1)
        @Schema(description = "수용 인원 수", example = "10")
        int capacity
) {}
