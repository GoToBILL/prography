package com.example.prography_project.room.dto;
import com.example.prography_project.room.domain.entity.Room;
import lombok.Getter;

@Getter
public class RoomResponseDto {
    private final int id;
    private final String title;
    private final int hostId;
    private final String roomType;
    private final String status;

    public RoomResponseDto(int id, String title, int hostId, String roomType, String status) {
        this.id = id;
        this.title = title;
        this.hostId = hostId;
        this.roomType = roomType;
        this.status = status;
    }

    public static RoomResponseDto from(Room room) {
        return new RoomResponseDto(
                room.getId(),
                room.getTitle(),
                room.getHost().getId(),
                room.getRoom_type().name(),
                room.getStatus().name()
        );
    }
}
