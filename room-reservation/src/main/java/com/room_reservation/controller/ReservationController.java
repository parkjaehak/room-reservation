package com.room_reservation.controller;

import com.room_reservation.domain.Reservation;
import com.room_reservation.security.SecurityUtil;
import com.room_reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import com.room_reservation.dto.CreateReservationRequest;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public Reservation create(@RequestBody CreateReservationRequest req) {
        SecurityUtil.requireUser();
        if (req.startAt() == null || req.endAt() == null || !req.startAt().isBefore(req.endAt())) {
            throw new IllegalArgumentException("invalid time range");
        }
        return reservationService.create(req.roomId(), req.startAt(), req.endAt());
    }

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable("id") Long id) {
        SecurityUtil.requireUser();
        reservationService.cancel(id);
    }

    
}


