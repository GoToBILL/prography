package com.example.prography_project.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreateRequestDto {
    private Long userId;
    private String roomType;
    private String title;
}