package com.room_reservation.repository;

import com.room_reservation.domain.Reservation;
import com.room_reservation.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRoomAndStartAtLessThanAndEndAtGreaterThan(
            Room room,
            OffsetDateTime endExclusive,
            OffsetDateTime startExclusive
    );
    List<Reservation> findByRoomAndStartAtBetween(Room room, OffsetDateTime start, OffsetDateTime end);
}


