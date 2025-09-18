package com.room_reservation.controller;

import com.room_reservation.security.SecurityUtil;
import com.room_reservation.service.ReservationService;
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
public class ReservationController {
    private final ReservationService reservationService;

    // 예약 생성 (user)
    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody CreateReservationRequest req) {
        SecurityUtil.requireUser();
        if (req.startAt() == null || req.endAt() == null || !req.startAt().isBefore(req.endAt())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다");
        }
        if (!req.startAt().toLocalDate().equals(req.endAt().toLocalDate())) {
            throw new IllegalArgumentException("예약은 동일 날짜 내에서만 가능합니다");
        }
        ReservationResponse responseDto = reservationService.create(req.roomId(), req.startAt(), req.endAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 예약 취소 (owner/admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable("id") Long id) {
        SecurityUtil.requireUser();
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}


