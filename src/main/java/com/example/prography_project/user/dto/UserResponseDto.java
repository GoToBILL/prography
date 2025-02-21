package com.example.prography_project.user.dto;

import com.example.prography_project.user.domain.entity.User;
import lombok.Data;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class UserResponseDto {
    private int id;
    private int fakerId;
    private String name;
    private String email;
    private String status;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.fakerId = user.getFakerId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.status = user.getStatus().name();
        this.createdAt = user.getCreated_at().format(FORMATTER);
        this.updatedAt = user.getUpdated_at().format(FORMATTER);
    }
}