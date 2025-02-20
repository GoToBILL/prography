package com.example.prography_project.room.service;

import com.example.prography_project.common.exception.BaseException;
import com.example.prography_project.common.exception.ErrorMessage;
import com.example.prography_project.common.response.ApiResponse;
import com.example.prography_project.room.domain.RoomStatus;
import com.example.prography_project.room.domain.RoomType;
import com.example.prography_project.room.domain.entity.Room;
import com.example.prography_project.room.dto.*;
import com.example.prography_project.room.repository.RoomRepository;
import com.example.prography_project.user.domain.UserStatus;
import com.example.prography_project.user.domain.entity.User;
import com.example.prography_project.user.repository.UserRepository;
import com.example.prography_project.useroom.domain.TeamType;
import com.example.prography_project.useroom.domain.entity.UserRoom;
import com.example.prography_project.useroom.repository.UserRoomRepository;
import com.example.prography_project.team.dto.TeamChangeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final GameService gameService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    /**
     * 방 생성 API
     * ✅ 조건:
     * - 유저가 존재해야 함.
     * - 유저 상태가 ACTIVE여야 함.
     * - 이미 방에 참가 중이면 방을 만들 수 없음.
     * - 방 생성 시 자동으로 방장은 RED 팀에 배정됨.
     *
     * @param requestDto 방 생성 요청 데이터
     * @return 성공 시 200 응답
     */
    public ApiResponse<Void> createRoom(RoomCreateRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BaseException(ErrorMessage.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BaseException(ErrorMessage.USER_NOT_ACTIVE);
        }

        if (userRoomRepository.existsByUserId(user.getId())) {
            throw new BaseException(ErrorMessage.USER_ALREADY_IN_ROOM);
        }

        Room room = new Room(
                requestDto.getTitle(),
                user,
                RoomType.valueOf(requestDto.getRoomType()),
                RoomStatus.WAIT
        );
        UserRoom userRoom = new UserRoom(room, user, TeamType.RED);

        roomRepository.save(room);
        userRoomRepository.save(userRoom);

        return ApiResponse.success();
    }

    /**
     * 모든 방 목록 조회 (페이징 처리)
     * ✅ 조건:
     * - 페이지 번호와 크기를 받아서 해당하는 방 목록을 가져옴.
     * - ID 기준 오름차순 정렬.
     *
     * @param page 페이지 번호
     * @param size 한 페이지에 포함될 방 개수
     * @return 방 목록 응답
     */
    public ApiResponse<RoomListResponseDto> getAllRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Room> roomPage = roomRepository.findAllByOrderByIdAsc(pageable);
        return ApiResponse.success(RoomListResponseDto.from(roomPage));
    }

    /**
     * 특정 방 상세 조회
     * ✅ 조건:
     * - 존재하지 않는 방 ID면 201 응답 반환.
     *
     * @param roomId 조회할 방 ID
     * @return 방 상세 정보 응답
     */
    public ApiResponse<RoomDetailResponseDto> getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ErrorMessage.ROOM_NOT_FOUND));
        return ApiResponse.success(RoomDetailResponseDto.from(room));
    }

    /**
     * 방 참가 API
     * ✅ 조건:
     * - 방이 존재해야 함.
     * - 방 상태가 WAIT(대기) 상태여야 참가 가능.
     * - 유저 상태가 ACTIVE여야 참가 가능.
     * - 유저가 이미 방에 있으면 참가 불가.
     * - 방 정원이 다 차면 참가 불가.
     * - RED 팀부터 우선 배정, RED가 가득 차면 BLUE로 배정.
     *
     * @param roomId 방 ID
     * @param requestDto 참가할 유저 정보
     * @return 성공 시 200 응답
     */
    public ApiResponse<Void> joinRoom(Long roomId, RoomJoinRequestDto requestDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ErrorMessage.ROOM_NOT_FOUND));

        if (room.getStatus() != RoomStatus.WAIT) {
            throw new BaseException(ErrorMessage.ROOM_NOT_JOINABLE);
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BaseException(ErrorMessage.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BaseException(ErrorMessage.USER_NOT_ACTIVE);
        }

        if (userRoomRepository.existsByUserId(user.getId())) {
            throw new BaseException(ErrorMessage.USER_ALREADY_IN_ROOM);
        }

        long currentMemberCount = userRoomRepository.countByRoomId(room.getId());
        long maxCapacity = room.getRoom_type().getMaxUserCount();

        if (currentMemberCount >= maxCapacity) {
            throw new BaseException(ErrorMessage.ROOM_FULL);
        }

        List<UserRoom> members = userRoomRepository.findAllByRoomId(room.getId());
        long redTeamCount = members.stream().filter(member -> member.getTeam() == TeamType.RED).count();

        TeamType assignedTeam = (redTeamCount == maxCapacity / 2) ? TeamType.BLUE : TeamType.RED;

        UserRoom userRoom = new UserRoom(room, user, assignedTeam);
        userRoomRepository.save(userRoom);

        return ApiResponse.success();
    }

    /**
     * 방 나가기 API
     * ✅ 조건:
     * - 방이 존재해야 함.
     * - 유저가 존재해야 함.
     * - 방이 진행 중(PROGRESS) 상태이거나 종료(FINISH) 상태면 나갈 수 없음.
     * - 호스트가 나가면 방이 FINISH 상태로 변경되고 모든 유저 퇴장.
     *
     * @param roomId 방 ID
     * @param requestDto 나갈 유저 정보
     * @return 성공 시 200 응답
     */
    public ApiResponse<Void> leaveRoom(Long roomId, RoomLeaveRequestDto requestDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ErrorMessage.ROOM_NOT_FOUND));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BaseException(ErrorMessage.USER_NOT_FOUND));

        if (room.getStatus() == RoomStatus.PROGRESS || room.getStatus() == RoomStatus.FINISH) {
            throw new BaseException(ErrorMessage.ROOM_NOT_JOINABLE);
        }

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(user.getId(), room.getId())
                .orElseThrow(() -> new BaseException(ErrorMessage.USER_NOT_IN_ROOM));

        if (room.getHost().getId().equals(user.getId())) {
            userRoomRepository.deleteByRoomId(room.getId());
            room.setStatus(RoomStatus.FINISH);
            roomRepository.save(room);
        } else {
            userRoomRepository.delete(userRoom);
        }

        return ApiResponse.success();
    }

    /**
     * 게임 시작 API
     * ✅ 조건:
     * - 존재하는 방이어야 함.
     * - 유저가 존재해야 함.
     * - 유저가 해당 방의 호스트여야 함.
     * - 방 정원이 다 찼어야 함.
     * - 방이 WAIT 상태여야 함.
     *
     * @param roomId     방 ID
     * @param requestDto 시작하려는 유저 정보
     * @return 성공 시 200 응답
     */
    public ApiResponse<Void> startGame(Long roomId, RoomStartRequestDto requestDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ErrorMessage.ROOM_NOT_FOUND));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BaseException(ErrorMessage.USER_NOT_FOUND));

        if (!room.getHost().getId().equals(user.getId())) {
            throw new BaseException(ErrorMessage.USER_NOT_HOST);
        }

        long currentMemberCount = userRoomRepository.countByRoomId(room.getId());
        long maxCapacity = room.getRoom_type().getMaxUserCount();

        if (currentMemberCount < maxCapacity) {
            throw new BaseException(ErrorMessage.ROOM_NOT_FULL);
        }

        if (room.getStatus() != RoomStatus.WAIT) {
            throw new BaseException(ErrorMessage.ROOM_NOT_WAITING);
        }

        // 게임 시작
        room.setStatus(RoomStatus.PROGRESS);

        // 1분 후 게임 자동 종료 스케줄링
        scheduleGameEnd(roomId);

        return ApiResponse.success();
    }
    private void scheduleGameEnd(Long roomId) {
        scheduler.schedule(() -> gameService.endGame(roomId), 1, TimeUnit.MINUTES);
    }

    /**
     * 팀 변경 API
     * ✅ 조건:
     * - 유저가 방에 참가한 상태여야 함.
     * - 현재 속한 팀에서 반대 팀으로 이동 (RED → BLUE / BLUE → RED).
     * - 이동하려는 팀이 이미 정원의 절반을 차지했다면 변경 불가.
     * - 방이 WAIT 상태여야 변경 가능.
     *
     * @param roomId 방 ID
     * @param requestDto 변경할 유저 정보
     * @return 성공 시 200 응답
     */
    public ApiResponse<Void> changeTeam(Long roomId, TeamChangeRequestDto requestDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ErrorMessage.ROOM_NOT_FOUND));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BaseException(ErrorMessage.USER_NOT_FOUND));

        if (room.getStatus() != RoomStatus.WAIT) {
            throw new BaseException(ErrorMessage.ROOM_NOT_WAITING);
        }

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(user.getId(), room.getId())
                .orElseThrow(() -> new BaseException(ErrorMessage.USER_NOT_IN_ROOM));

        TeamType currentTeam = userRoom.getTeam();
        TeamType newTeam = (currentTeam == TeamType.RED) ? TeamType.BLUE : TeamType.RED;

        long currentTeamCount = userRoomRepository.countByRoomIdAndTeam(room.getId(), newTeam);
        long maxTeamSize = room.getRoom_type().getMaxUserCount() / 2;

        if (currentTeamCount >= maxTeamSize) {
            throw new BaseException(ErrorMessage.USER_NOT_CHANGE_TEAM);
        }

        userRoom.setTeam(newTeam);

        return ApiResponse.success();
    }
}
