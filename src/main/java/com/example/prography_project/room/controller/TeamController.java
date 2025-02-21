package com.example.prography_project.room.controller;
import com.example.prography_project.room.dto.TeamChangeRequestDto;
import com.example.prography_project.common.response.ApiResponse;
import com.example.prography_project.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final RoomService roomService;

    @PutMapping("/{roomId}")
    public ApiResponse<Void> changeTeam(@PathVariable int roomId, @RequestBody TeamChangeRequestDto requestDto) {
        return roomService.changeTeam(roomId, requestDto);
    }
}
