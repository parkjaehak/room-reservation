package com.room_reservation.service;

import com.room_reservation.domain.Room;
import com.room_reservation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    @Transactional
    public Room createRoom(String name, String location, int capacity) {
        Room room = Room.builder()
                .name(name)
                .location(location)
                .capacity(capacity)
                .build();
        return roomRepository.save(room);
    }
}


