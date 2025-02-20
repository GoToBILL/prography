package com.example.prography_project.room.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomType {
    SINGLE(2), DOUBLE(4);

    private final int maxUserCount;
}
