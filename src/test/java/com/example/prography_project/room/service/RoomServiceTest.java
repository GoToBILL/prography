package com.example.prography_project.room.service;

import com.example.prography_project.common.exception.BaseException;
import com.example.prography_project.common.response.ApiResponse;
import com.example.prography_project.init.service.InitService;
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
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private InitService initService;

    @Autowired
    private EntityManager em;

    private User testUser;
    private Room testRoom;
    private User newUser;
    private User inactiveUser;
    private User new2User;

    @BeforeEach
    void setup() {
        initService.initialize(100, 61); // 예제: 시드 100, 유저 50명

        // 활성 상태인 첫 번째 유저를 방장으로 설정
        testUser = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("테스트 유저가 존재하지 않습니다."));

        // 방 생성 및 저장
        testRoom = new Room("테스트 방", testUser, RoomType.SINGLE, RoomStatus.WAIT);
        roomRepository.save(testRoom);

        // 방장 방 참가
        roomService.joinRoom(testRoom.getId(), new RoomJoinRequestDto(testUser.getId()));

        // 새로운 활성 유저 생성 및 저장
        newUser = new User(2L, "새로운 유저", "newuser@example.com");
        newUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(newUser);

        // 새로운 활성 유저 방 참가
        roomService.joinRoom(testRoom.getId(), new RoomJoinRequestDto(newUser.getId()));

        // 비활성 유저 생성 및 저장
        inactiveUser = new User(3L, "비활성 유저", "inactiveuser@example.com");
        inactiveUser.setStatus(UserStatus.NON_ACTIVE);
        userRepository.save(inactiveUser);

        new2User = new User(4L, "새로운 유저2", "asd@asd");
        new2User.setStatus(UserStatus.ACTIVE);
        userRepository.save(new2User);
    }


    @Test
    void 유저가_존재하지_않으면_방_생성_실패() {
        // given
        RoomCreateRequestDto requestDto = new RoomCreateRequestDto(9999L, "테스트 방", "SINGLE"); // 존재하지 않는 ID

        // when & then
        assertThrows(BaseException.class, () -> roomService.createRoom(requestDto));
    }

    @Test
    void 꽉_찬_방에_들어갈_수_없다() {
        assertThrows(BaseException.class, () -> roomService.joinRoom(testRoom.getId(), new RoomJoinRequestDto(new2User.getId())));
    }

    @Test
    void 유저가_ACTIVE_상태가_아니면_방_생성_실패() {
        // given
        User inactiveUser = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == UserStatus.NON_ACTIVE)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("테스트 유저가 존재하지 않습니다."));

        RoomCreateRequestDto requestDto = new RoomCreateRequestDto(inactiveUser.getId(), "테스트 방", "SINGLE");

        // when & then
        assertThrows(BaseException.class, () -> roomService.createRoom(requestDto));
    }

    @Test
    void 유저가_이미_방에_참가중이면_방_생성_실패() {
        // given
        Room testRoom = new Room("테스트 방", testUser, RoomType.SINGLE, RoomStatus.WAIT);
        UserRoom testUserRoom = new UserRoom(testRoom, testUser, TeamType.RED);

        roomRepository.save(testRoom);
        userRoomRepository.save(testUserRoom);

        RoomCreateRequestDto requestDto = new RoomCreateRequestDto(testUser.getId(), "테스트 방", "SINGLE");
        // when & then
        assertThrows(BaseException.class, () -> roomService.createRoom(requestDto));
    }

    @Test
    void 존재하는_방을_조회하면_정상적으로_응답_반환() {
        // given
        Room testRoom = new Room("테스트 방", testUser, RoomType.SINGLE, RoomStatus.WAIT);
        roomRepository.save(testRoom);

        // when
        ApiResponse<RoomDetailResponseDto> response = roomService.getRoomById(testRoom.getId());

        // then
        assertNotNull(response);
        assertEquals(testRoom.getId(), response.getResult().getId());
    }

    @Test
    void 존재하지_않는_방을_조회하면_예외_발생() {
        // given
        Long nonExistentRoomId = 9999L;

        // when & then
        assertThrows(BaseException.class, () -> roomService.getRoomById(nonExistentRoomId));
    }

    @Test
    void 유저가_방에_참가하면_정상적으로_참가됨() {
        // given
        Room testRoom = new Room("테스트 방", testUser, RoomType.SINGLE, RoomStatus.WAIT);
        UserRoom testUserRoom = new UserRoom(testRoom, testUser, TeamType.RED);
        roomRepository.save(testRoom);
        userRoomRepository.save(testUserRoom);

        User newUser = new User(2L, "새로운 유저", "newuser@example.com");
        newUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(newUser);

        RoomJoinRequestDto requestDto = new RoomJoinRequestDto(newUser.getId());

        // when
        ApiResponse<Void> response = roomService.joinRoom(testRoom.getId(), requestDto);

        // then
        assertNotNull(response);
        assertTrue(userRoomRepository.existsByUserId(newUser.getId()));
    }

    @Test
    void 존재하지_않는_방에_참가하면_예외_발생() {
        // given
        RoomJoinRequestDto requestDto = new RoomJoinRequestDto(testUser.getId());
        Long nonExistentRoomId = 9999L;

        // when & then
        assertThrows(BaseException.class, () -> roomService.joinRoom(nonExistentRoomId, requestDto));
    }

    @Test
    void 진행중이거나_종료된_방에는_참가_불가() {
        // given
        Room testRoom = new Room("테스트 방", testUser, RoomType.SINGLE, RoomStatus.PROGRESS);
        roomRepository.save(testRoom);

        RoomJoinRequestDto requestDto = new RoomJoinRequestDto(testUser.getId());

        // when & then
        assertThrows(BaseException.class, () -> roomService.joinRoom(testRoom.getId(), requestDto));
    }

    @Test
    void 방장_유저가_이미_방에_참가중이면_참가_불가() {
        // given
        Room testRoom = new Room("테스트 방", testUser, RoomType.SINGLE, RoomStatus.WAIT);
        UserRoom testUserRoom = new UserRoom(testRoom, testUser, TeamType.RED);
        roomRepository.save(testRoom);
        userRoomRepository.save(testUserRoom);

        RoomJoinRequestDto requestDto = new RoomJoinRequestDto(testUser.getId());

        // when & then
        assertThrows(BaseException.class, () -> roomService.joinRoom(testRoom.getId(), requestDto));
    }

    @Test
    void 두_명의_방장이_각각_방을_개설했고_유저가_한_방에_참여한_상태에서_다른_방에_참가하면_예외_발생() {
        // given
        User host1 = new User(1L, "방장1", "host1@example.com");
        User host2 = new User(2L, "방장2", "host2@example.com");
        User participant = new User(3L, "참여자", "participant@example.com");

        host1.setStatus(UserStatus.ACTIVE);
        host2.setStatus(UserStatus.ACTIVE);
        participant.setStatus(UserStatus.ACTIVE);

        Room room1 = new Room("방1", host1, RoomType.SINGLE, RoomStatus.WAIT);
        Room room2 = new Room("방2", host2, RoomType.SINGLE, RoomStatus.WAIT);

        UserRoom userRoom = new UserRoom(room1, participant, TeamType.RED);

        userRepository.save(host1);
        userRepository.save(host2);
        userRepository.save(participant);
        roomRepository.save(room1);
        roomRepository.save(room2);
        userRoomRepository.save(userRoom); // 유저가 첫 번째 방에 참가한 상태

        RoomJoinRequestDto requestDto = new RoomJoinRequestDto(participant.getId());

        // when & then
        assertThrows(BaseException.class, () -> roomService.joinRoom(room2.getId(), requestDto));
    }


    @Test
    void 방에_2명이_있을때_유저가_나가면_방장만_남음() {
        // given
        RoomLeaveRequestDto requestDto = new RoomLeaveRequestDto(newUser.getId());

        // when
        roomService.leaveRoom(testRoom.getId(), requestDto);

        // then
        long remainingUserRooms = userRoomRepository.countByRoomId(testRoom.getId());
        assertEquals(1, remainingUserRooms); // 방장만 남았으므로 count가 1이어야 함
    }


    @Test
    void 존재하지_않는_방에서_나가면_예외_발생() {
        // given
        RoomLeaveRequestDto requestDto = new RoomLeaveRequestDto(testUser.getId());
        Long nonExistentRoomId = 9999L;

        // when & then
        assertThrows(BaseException.class, () -> roomService.leaveRoom(nonExistentRoomId, requestDto));
    }

    @Test
    void 진행중이거나_종료된_방에서는_나가기_불가() {
        // given
        testRoom.setStatus(RoomStatus.PROGRESS);
        RoomLeaveRequestDto requestDto = new RoomLeaveRequestDto(newUser.getId());

        // when & then
        assertThrows(BaseException.class, () -> roomService.leaveRoom(testRoom.getId(), requestDto));
    }

    @Test
    void 방장이_나가면_방이_종료됨() {
        RoomLeaveRequestDto requestDto = new RoomLeaveRequestDto(testUser.getId());

        // when
        ApiResponse<Void> response = roomService.leaveRoom(testRoom.getId(), requestDto);

        // then
        assertNotNull(response);
        assertEquals(RoomStatus.FINISH, testRoom.getStatus());
        assertTrue(userRoomRepository.findAllByRoomId(testRoom.getId()).isEmpty());
    }

    @Test
    void 방장이_아닌_유저가_게임을_시작하려하면_예외_발생() {
        // given
        RoomStartRequestDto requestDto = new RoomStartRequestDto(newUser.getId()); // 방장이 아닌 유저

        // when & then
        assertThrows(BaseException.class, () -> roomService.startGame(testRoom.getId(), requestDto));
    }

    @Test
    void 정원이_꽉_차지_않으면_게임을_시작할_수_없음() {
        // given
        RoomStartRequestDto requestDto = new RoomStartRequestDto(testUser.getId()); // 방장
        roomService.leaveRoom(testRoom.getId(), new RoomLeaveRequestDto(newUser.getId()));
        // when & then
        assertThrows(BaseException.class, () -> roomService.startGame(testRoom.getId(), requestDto));
    }

    @Test
    void 게임이_이미_진행중이면_게임을_시작할_수_없음() {
        // given
        testRoom.setStatus(RoomStatus.PROGRESS);
        roomRepository.save(testRoom);
        RoomStartRequestDto requestDto = new RoomStartRequestDto(testUser.getId());

        // when & then
        assertThrows(BaseException.class, () -> roomService.startGame(testRoom.getId(), requestDto));
    }

    @Test
    void 방장이_정원이_꽉_찬_방에서_게임을_시작하면_정상적으로_시작됨() {
        RoomStartRequestDto requestDto = new RoomStartRequestDto(testUser.getId());

        // when
        ApiResponse<Void> response = roomService.startGame(testRoom.getId(), requestDto);

        // then
        assertNotNull(response);
        assertEquals(RoomStatus.PROGRESS, testRoom.getStatus());
    }

    @Test
    void 팀을_변경하려_했지만_정원의_절반이_가득_차면_변경_불가() {
        // given
        UserRoom existingUserRoom = userRoomRepository.findByUserIdAndRoomId(newUser.getId(), testRoom.getId())
                .orElseThrow(() -> new RuntimeException("테스트 유저룸이 존재하지 않습니다."));

        // 현재 상태는 RED 1명, BLUE 1명으로 균형이 맞춰진 상태
        TeamChangeRequestDto requestDto = new TeamChangeRequestDto(newUser.getId());

        // when & then - 변경이 불가능해야 함
        assertThrows(BaseException.class, () -> roomService.changeTeam(testRoom.getId(), requestDto));
    }

    @Test
    void 팀을_변경하려_했지만_정원의_절반이_가득_차면_변경_X() {
        roomService.leaveRoom(testRoom.getId(), new RoomLeaveRequestDto(newUser.getId()));
        // 현재 상태는 RED 1명, BLUE 1명으로 균형이 맞춰진 상태
        TeamChangeRequestDto requestDto = new TeamChangeRequestDto(newUser.getId());

        // when & then - 변경이 불가능해야 함
        assertThrows(BaseException.class, () -> roomService.changeTeam(testRoom.getId(), requestDto));
    }

    @Test
    void 새로운_DOUBLE_방에서_팀을_변경하면_정상적으로_반영됨() {
        // given
        User additionalUser = new User(5L, "새로운 유저2", "asd@asd");
        additionalUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(additionalUser);

        Room doubleRoom = new Room("새로운 DOUBLE 방", new2User, RoomType.DOUBLE, RoomStatus.WAIT);
        roomRepository.save(doubleRoom);

        // new2User와 additionalUser를 각기 다른 팀으로 배정
        userRoomRepository.save(new UserRoom(doubleRoom, new2User, TeamType.RED));
        userRoomRepository.save(new UserRoom(doubleRoom, additionalUser, TeamType.BLUE));

        TeamChangeRequestDto requestDto = new TeamChangeRequestDto(new2User.getId());

        // when
        ApiResponse<Void> response = roomService.changeTeam(doubleRoom.getId(), requestDto);

        // then
        assertNotNull(response);
        UserRoom updatedUserRoom = userRoomRepository.findByUserIdAndRoomId(new2User.getId(), doubleRoom.getId())
                .orElseThrow(() -> new RuntimeException("유저룸을 찾을 수 없습니다."));
        assertNotEquals(TeamType.RED, updatedUserRoom.getTeam()); // RED -> BLUE 변경 확인
    }


    @Test
    void 존재하지_않는_방에서_팀_변경을_시도하면_예외_발생() {
        // given
        TeamChangeRequestDto requestDto = new TeamChangeRequestDto(newUser.getId());
        Long nonExistentRoomId = 9999L;

        // when & then
        assertThrows(BaseException.class, () -> roomService.changeTeam(nonExistentRoomId, requestDto));
    }

    @Test
    void 존재하지_않는_유저가_팀_변경을_시도하면_예외_발생() {
        // given
        TeamChangeRequestDto requestDto = new TeamChangeRequestDto(9999L); // 존재하지 않는 유저 ID

        // when & then
        assertThrows(BaseException.class, () -> roomService.changeTeam(testRoom.getId(), requestDto));
    }

    @Test
    void 게임이_이미_진행중이면_팀을_변경할_수_없음() {
        // given
        User additionalUser = new User(5L, "새로운 유저2", "asd@asd");
        additionalUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(additionalUser);

        Room doubleRoom = new Room("새로운 DOUBLE 방", new2User, RoomType.DOUBLE, RoomStatus.WAIT);
        roomRepository.save(doubleRoom);

        // new2User와 additionalUser를 각기 다른 팀으로 배정
        userRoomRepository.save(new UserRoom(doubleRoom, new2User, TeamType.RED));
        userRoomRepository.save(new UserRoom(doubleRoom, additionalUser, TeamType.BLUE));
        doubleRoom.setStatus(RoomStatus.PROGRESS);

        // when & then
        assertThrows(BaseException.class, () -> roomService.changeTeam(doubleRoom.getId(), new TeamChangeRequestDto(new2User.getId())));
    }

    @Test
    void 유저가_해당_방에_참가하지_않으면_팀_변경_불가() {
        // given
        TeamChangeRequestDto requestDto = new TeamChangeRequestDto(inactiveUser.getId()); // 방에 참가하지 않은 유저

        // when & then
        assertThrows(BaseException.class, () -> roomService.changeTeam(testRoom.getId(), requestDto));
    }
}
