package com.room_reservation.controller;

import com.room_reservation.dto.AvailabilityResponse;
import com.room_reservation.dto.RoomResponse;
import com.room_reservation.security.SecurityUtil;
import com.room_reservation.service.RoomService;
import com.room_reservation.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "회의실 관리", description = "회의실 등록 및 가용성 조회 API")
public class RoomController {
    private final RoomService roomService;
    private final AvailabilityService availabilityService;

    @Operation(
        summary = "회의실 등록",
        description = "새로운 회의실을 등록합니다. Admin 권한이 필요합니다."
    )
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest req) {
        SecurityUtil.requireAdmin();
        RoomResponse responseDto = roomService.createRoom(req.name(), req.location(), req.capacity());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
        summary = "가용성 조회",
        description = "특정 날짜의 모든 회의실 가용성을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> getAvailability(
        @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", example = "2025-09-18", required = true)
        @RequestParam("date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        SecurityUtil.requireUser();
        return ResponseEntity.ok(availabilityService.getDaily(date));
    }
}


