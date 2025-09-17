package com.room_reservation.controller;

import com.room_reservation.domain.Room;
import com.room_reservation.security.SecurityUtil;
import com.room_reservation.service.RoomService;
import com.room_reservation.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import com.room_reservation.dto.CreateRoomRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final AvailabilityService availabilityService;

    @PostMapping
    public Room createRoom(@RequestBody CreateRoomRequest req) {
        SecurityUtil.requireAdmin();
        return roomService.createRoom(req.name(), req.location(), req.capacity());
    }

    @GetMapping
    public Object getAvailability(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availabilityService.getDaily(date);
    }

    
}


