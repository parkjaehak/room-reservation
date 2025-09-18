package com.room_reservation.repository;

import com.room_reservation.domain.Reservation;
import com.room_reservation.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 해당 날짜의 예약 조회
    List<Reservation> findByRoomAndStartAtBetween(Room room, OffsetDateTime start, OffsetDateTime end);
    
    // 시간 겹침 검증용 - 겹치는 예약이 있는지 확인
    List<Reservation> findByRoomAndStartAtLessThanAndEndAtGreaterThan(Room room, OffsetDateTime endExclusive, OffsetDateTime startExclusive);
}


