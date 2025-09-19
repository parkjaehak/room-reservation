package com.room_reservation.dto;

public record RoomResponse(
        Long id,
        String name,
        String location,
        Integer capacity
) {
}
