package com.example.prography_project.user.controller;

import com.example.prography_project.common.response.ApiResponse;
import com.example.prography_project.user.dto.UserListResponseDto;
import com.example.prography_project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ApiResponse<UserListResponseDto> getAllUsers(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.success(userService.getAllUsers(page, size));
    }
}