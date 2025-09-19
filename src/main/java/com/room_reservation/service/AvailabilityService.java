package com.room_reservation.service;

import com.room_reservation.domain.Reservation;
import com.room_reservation.domain.Room;
import com.room_reservation.dto.AvailabilityResponse;
import com.room_reservation.repository.ReservationRepository;
import com.room_reservation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public List<AvailabilityResponse> getDaily(LocalDate date) {
        // 한국 시간대(UTC+9) 사용
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        OffsetDateTime dayStart = date.atStartOfDay(koreaZone).toOffsetDateTime();
        OffsetDateTime dayEnd = dayStart.plusDays(1);

        List<Room> rooms = roomRepository.findAll();
        List<AvailabilityResponse> result = new ArrayList<>();
        for (Room room : rooms) {
            // 해당 날짜 예약 현황/빈 시간대 조회
            List<Reservation> reservations = reservationRepository.findByRoomAndStartAtBetween(room, dayStart, dayEnd)
                    .stream()
                    .sorted(Comparator.comparing(Reservation::getStartAt))
                    .toList();

            // 예약된 시간대 목록 생성 (한국 시간대로 변환)
            List<AvailabilityResponse.TimeRange> reserved = new ArrayList<>();
            for (Reservation reservation : reservations) {
                // UTC 시간을 한국 시간대로 변환
                OffsetDateTime startAtKorea = reservation.getStartAt().atZoneSameInstant(koreaZone).toOffsetDateTime();
                OffsetDateTime endAtKorea = reservation.getEndAt().atZoneSameInstant(koreaZone).toOffsetDateTime();
                reserved.add(new AvailabilityResponse.TimeRange(startAtKorea, endAtKorea));
            }

            //예약된 시간 사이 빈 구간 조회
            List<AvailabilityResponse.TimeRange> free = new ArrayList<>();
            OffsetDateTime currTime = dayStart;
            for (AvailabilityResponse.TimeRange timeRange : reserved) {
                // 현재 시간과 예약 시작 사이에 빈 시간이 있으면 추가
                if (currTime.isBefore(timeRange.startAt())) {
                    free.add(new AvailabilityResponse.TimeRange(currTime, timeRange.startAt()));
                }
                // 현재 시간을 예약 종료 시점으로 이동
                if (currTime.isBefore(timeRange.endAt())) {
                    currTime = timeRange.endAt();
                }
            }
            // 마지막 예약 이후 남은 시간이 있으면 추가
            if (currTime.isBefore(dayEnd)) {
                free.add(new AvailabilityResponse.TimeRange(currTime, dayEnd));
            }

            result.add(new AvailabilityResponse(
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


