package com.example.prography_project.room.dto;
import com.example.prography_project.room.domain.entity.Room;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class RoomDetailResponseDto {
    private final int id;
    private final String title;
    private final int hostId;
    private final String roomType;
    private final String status;
    private final String createdAt;
    private final String updatedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RoomDetailResponseDto(Room room) {
        this.id = room.getId();
        this.title = room.getTitle();
        this.hostId = room.getHost().getId();
        this.roomType = room.getRoom_type().name();
        this.status = room.getStatus().name();
        this.createdAt = room.getCreated_at().format(FORMATTER);
        this.updatedAt = room.getUpdated_at().format(FORMATTER);
    }

    public static RoomDetailResponseDto from(Room room) {
        return new RoomDetailResponseDto(room);
    }
}

