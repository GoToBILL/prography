package com.example.prography_project.room.dto;
import com.example.prography_project.room.domain.entity.Room;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RoomListResponseDto {
    private final int totalElements;
    private final int totalPages;
    private final List<RoomResponseDto> roomList;

    public RoomListResponseDto(long totalElements, int totalPages, List<RoomResponseDto> roomList) {
        this.totalElements = (int) totalElements;
        this.totalPages = totalPages;
        this.roomList = roomList;
    }

    public static RoomListResponseDto from(Page<Room> roomPage) {
        return new RoomListResponseDto(
                roomPage.getTotalElements(),
                roomPage.getTotalPages(),
                roomPage.getContent().stream()
                        .map(RoomResponseDto::from)
                        .collect(Collectors.toList())
        );
    }
}
