package com.room_reservation.service;

import com.room_reservation.domain.Reservation;
import com.room_reservation.domain.Room;
import com.room_reservation.dto.AvailabilityDto;
import com.room_reservation.repository.ReservationRepository;
import com.room_reservation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public List<AvailabilityDto> getDaily(LocalDate date) {
        OffsetDateTime dayStart = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime dayEnd = dayStart.plusDays(1);

        List<Room> rooms = roomRepository.findAll();
        List<AvailabilityDto> result = new ArrayList<>();
        for (Room room : rooms) {
            List<Reservation> reservations = reservationRepository.findByRoomAndStartAtBetween(room, dayStart, dayEnd)
                    .stream()
                    .sorted(Comparator.comparing(Reservation::getStartAt))
                    .collect(Collectors.toList());

            List<AvailabilityDto.TimeRange> reserved = new ArrayList<>();
            for (Reservation r : reservations) {
                reserved.add(new AvailabilityDto.TimeRange(r.getStartAt(), r.getEndAt()));
            }

            List<AvailabilityDto.TimeRange> free = new ArrayList<>();
            OffsetDateTime cursor = dayStart;
            for (AvailabilityDto.TimeRange tr : reserved) {
                if (cursor.isBefore(tr.startAt)) {
                    free.add(new AvailabilityDto.TimeRange(cursor, tr.startAt));
                }
                if (cursor.isBefore(tr.endAt)) {
                    cursor = tr.endAt;
                }
            }
            if (cursor.isBefore(dayEnd)) {
                free.add(new AvailabilityDto.TimeRange(cursor, dayEnd));
            }

            result.add(new AvailabilityDto(
                    room.getId(),
                    room.getName(),
                    room.getLocation(),
                    room.getCapacity(),
                    reserved,
                    free
            ));
        }
        return result;
    }
}


