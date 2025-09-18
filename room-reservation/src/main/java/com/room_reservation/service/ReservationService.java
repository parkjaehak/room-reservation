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
        Room room = roomRepository.findById(roomId).orElseThrow(NotFoundException::new);

        RequestUser user = RequestUserHolder.get();
        if (user == null || user.userId() == null) {
            throw new ForbiddenException();
        }
        
        // 시간 겹침 검증 - 동일 방에서 겹치는 시간이 있는지 확인
        if (hasOverlappingReservation(room, startAt, endAt)) {
            throw new IllegalArgumentException("해당 시간대에 이미 예약이 있습니다");
        }
        
        Reservation reservation = Reservation.builder()
                .room(room)
                .userId(user.userId())
                .startAt(startAt)
                .endAt(endAt)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // 엔티티를 DTO로 변환
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
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(NotFoundException::new);
        RequestUser user = RequestUserHolder.get();

        if (user == null) {
            throw new ForbiddenException();
        }
        // Admin 이거나 예약 소유자만 취소 가능
        if (user.role() != Role.ADMIN && !user.userId().equals(reservation.getUserId())) {
            throw new ForbiddenException();
        }
        reservationRepository.delete(reservation);
    }
    
    // 시간 겹침 검증 메서드
    private boolean hasOverlappingReservation(Room room, OffsetDateTime startAt, OffsetDateTime endAt) {
        // 겹치는 예약이 있는지 확인: (startAt < existing.endAt) && (endAt > existing.startAt)
        return !reservationRepository.findByRoomAndStartAtLessThanAndEndAtGreaterThan(room, endAt, startAt).isEmpty();
    }
}


