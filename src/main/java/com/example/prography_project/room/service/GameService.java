package com.example.prography_project.room.service;

import com.example.prography_project.room.domain.RoomStatus;
import com.example.prography_project.room.domain.entity.Room;
import com.example.prography_project.room.repository.RoomRepository;
import com.example.prography_project.useroom.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    @Transactional
    public void endGame(Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null && room.getStatus() == RoomStatus.PROGRESS) {
            room.setStatus(RoomStatus.FINISH);
            roomRepository.save(room);

            // 유저-방 관계 삭제
            userRoomRepository.deleteByRoomId(room.getId());
        }
    }
}
