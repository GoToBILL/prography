package com.example.prography_project.init.controller;

import com.example.prography_project.common.response.ApiResponse;
import com.example.prography_project.init.dto.InitRequestDto;
import com.example.prography_project.init.service.InitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InitController {

    private final InitService initService;

    @PostMapping("/init")
    public ApiResponse<Void> initializeDatabase(@RequestBody InitRequestDto requestDto) {
        initService.initialize(requestDto.getSeed(), requestDto.getQuantity());
        return ApiResponse.success();
    }
}