package com.room_reservation.service;

import com.room_reservation.domain.Reservation;
import com.room_reservation.domain.Room;
import com.room_reservation.dto.ReservationResponse;
import com.room_reservation.exception.ForbiddenException;
import com.room_reservation.exception.NotFoundException;
import com.room_reservation.repository.ReservationRepository;
import com.room_reservation.repository.RoomRepository;
import com.room_reservation.security.RequestUser;
import com.room_reservation.security.RequestUserHolder;
import com.room_reservation.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;


@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public ReservationResponse create(Long roomId, OffsetDateTime startAt, OffsetDateTime endAt) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new NotFoundException("방을 찾을 수 없습니다."));


        RequestUser user = RequestUserHolder.get();
        if (user == null || user.userId() == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        
        Reservation reservation = Reservation.builder()
                .room(room)
                .userId(user.userId())
                .startAt(startAt)
                .endAt(endAt)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(
                savedReservation.getId(),
                savedReservation.getRoom().getId(),
                savedReservation.getRoom().getName(),
                savedReservation.getRoom().getLocation(),
                savedReservation.getRoom().getCapacity(),
                savedReservation.getUserId(),
                savedReservation.getStartAt(),
                savedReservation.getEndAt(),
                savedReservation.getCreatedAt()
        );
    }

    @Transactional
    public void cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("예약 정보를 찾을 수 없습니다."));
        RequestUser user = RequestUserHolder.get();

        if (user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        // Admin 이거나 예약 소유자만 취소 가능
        if (user.role() != Role.ADMIN && !user.userId().equals(reservation.getUserId())) {
            throw new ForbiddenException("해당 자원에 접근할 권한이 없습니다.");
        }
        reservationRepository.delete(reservation);
    }
}


