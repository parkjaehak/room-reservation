package com.room_reservation.controller;

import com.room_reservation.security.SecurityUtil;
import com.room_reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.room_reservation.dto.CreateReservationRequest;
import com.room_reservation.dto.ReservationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "예약 관리", description = "회의실 예약 생성 및 취소 API")
public class ReservationController {
    private final ReservationService reservationService;

    @Operation(
        summary = "예약 생성",
        description = "새로운 회의실 예약을 생성합니다. User 권한이 필요합니다."
    )
    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody CreateReservationRequest req) {
        SecurityUtil.requireUser();
        if (!req.startAt().isBefore(req.endAt())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다");
        }

        ReservationResponse responseDto = reservationService.create(req.roomId(), req.startAt(), req.endAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
        summary = "예약 취소",
        description = "기존 예약을 취소합니다. 예약 소유자 또는 Admin 권한이 필요합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
        @Parameter(description = "취소할 예약 ID", example = "1", required = true)
        @PathVariable("id") Long id) {
        SecurityUtil.requireUser();
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}


