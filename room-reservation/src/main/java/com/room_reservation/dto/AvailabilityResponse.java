package com.room_reservation.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record AvailabilityResponse(
        Long roomId,
        String roomName,
        String location,
        Integer capacity,
        //예약 현황
        List<TimeRange> reservations,
        //빈 시간대 조회
        List<TimeRange> freeSlots) {

    public record TimeRange(
            OffsetDateTime startAt,
            OffsetDateTime endAt) {
    }
}


