package com.room_reservation.service;

import com.room_reservation.domain.Reservation;
import com.room_reservation.domain.Room;
import com.room_reservation.exception.ForbiddenException;
import com.room_reservation.exception.NotFoundException;
import com.room_reservation.repository.ReservationRepository;
import com.room_reservation.repository.RoomRepository;
import com.room_reservation.security.RequestUser;
import com.room_reservation.security.RequestUserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Reservation create(Long roomId, OffsetDateTime startAt, OffsetDateTime endAt) {
        Room room = roomRepository.findById(roomId).orElseThrow(NotFoundException::new);
        RequestUser user = RequestUserHolder.get();
        if (user == null || user.getUserId() == null) throw new ForbiddenException();
        Reservation r = Reservation.builder()
                .room(room)
                .userId(user.getUserId())
                .startAt(startAt)
                .endAt(endAt)
                .build();
        return reservationRepository.save(r);
    }

    @Transactional
    public void cancel(Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId).orElseThrow(NotFoundException::new);
        RequestUser user = RequestUserHolder.get();
        if (user == null) throw new ForbiddenException();
        if (user.getRole() != com.room_reservation.security.Role.ADMIN && !user.getUserId().equals(r.getUserId())) {
            throw new ForbiddenException();
        }
        reservationRepository.delete(r);
    }

    public List<Reservation> findByDate(Long roomId, LocalDate date) {
        Room room = roomRepository.findById(roomId).orElseThrow(NotFoundException::new);
        OffsetDateTime start = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = start.plusDays(1);
        return reservationRepository.findByRoomAndStartAtBetween(room, start, end);
    }
}


