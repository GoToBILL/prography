package com.example.prography_project.user.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class UserListResponseDto {
    private int totalElements;
    private int totalPages;
    private List<UserResponseDto> userList;

    public UserListResponseDto(Page<UserResponseDto> userPage) {
        this.totalElements = (int) userPage.getTotalElements();
        this.totalPages = userPage.getTotalPages();
        this.userList = userPage.getContent();
    }
}