package com.example.prography_project.room.controller;

import com.example.prography_project.common.response.ApiResponse;
import com.example.prography_project.room.dto.*;
import com.example.prography_project.room.service.RoomService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ApiResponse<Void> createRoom(@RequestBody RoomCreateRequestDto requestDto) {
        return roomService.createRoom(requestDto);
    }

    @GetMapping
    public ApiResponse<RoomListResponseDto> getAllRooms(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page) {
        return roomService.getAllRooms(page, size);
    }

    @GetMapping("/{roomId}")
    public ApiResponse<RoomDetailResponseDto> getRoomDetail(@PathVariable int roomId) {
        return roomService.getRoomById(roomId);
    }

    @PostMapping("/attention/{roomId}")
    public ApiResponse<Void> joinRoom(@PathVariable int roomId, @RequestBody RoomJoinRequestDto requestDto) {
        return roomService.joinRoom(roomId, requestDto);
    }

    @PostMapping("/out/{roomId}")
    public ApiResponse<Void> leaveRoom(@PathVariable int roomId, @RequestBody RoomLeaveRequestDto requestDto) {
        return roomService.leaveRoom(roomId, requestDto);
    }

    @PutMapping("/start/{roomId}")
    public ApiResponse<Void> startGame(
            @PathVariable int roomId, @RequestBody RoomStartRequestDto requestDto) {
        return roomService.startGame(roomId, requestDto);
    }
}