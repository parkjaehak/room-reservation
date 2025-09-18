package com.room_reservation.controller;

import com.room_reservation.dto.AvailabilityResponse;
import com.room_reservation.dto.RoomResponse;
import com.room_reservation.security.SecurityUtil;
import com.room_reservation.service.RoomService;
import com.room_reservation.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.room_reservation.dto.CreateRoomRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final AvailabilityService availabilityService;

    // 회의실 등록 (admin)
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest req) {
        SecurityUtil.requireAdmin();
        RoomResponse responseDto = roomService.createRoom(req.name(), req.location(), req.capacity());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 가용성 조회
    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> getAvailability(
        @RequestParam("date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getDaily(date));
    }
}


