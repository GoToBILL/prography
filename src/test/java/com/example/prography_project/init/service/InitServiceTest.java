package com.example.prography_project.init.service;

import com.example.prography_project.room.repository.RoomRepository;
import com.example.prography_project.user.domain.UserStatus;
import com.example.prography_project.user.domain.entity.User;
import com.example.prography_project.user.repository.UserRepository;
import com.example.prography_project.useroom.repository.UserRoomRepository;
import com.example.prography_project.util.FakerApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class InitServiceTest {

    @Autowired
    private InitService testService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private FakerApiClient fakerApiClient;

    @BeforeEach
    void setUp() {
        userRoomRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void DB에_90명이_잘_들어가_있어야_하고_상태도_잘_넣어져야함() {
        // Given (가짜 사용자 데이터 생성)
        int seed = 1;
        int quantity = 90;

        // When (초기화 실행)
        testService.initialize(seed, quantity);

        // Then (저장된 유저 검증)
        List<User> savedUsers = userRepository.findAll();

        assertThat(savedUsers).hasSize(90); // 3명의 유저가 저장되어야 함

        assertThat(savedUsers)
                .filteredOn(user -> user.getFakerId() <= 30)
                .allMatch(user -> user.getStatus() == UserStatus.ACTIVE);

        assertThat(savedUsers)
                .filteredOn(user -> user.getFakerId() > 30 && user.getFakerId() <= 60)
                .allMatch(user -> user.getStatus() == UserStatus.WAIT);

        assertThat(savedUsers)
                .filteredOn(user -> user.getFakerId() > 60)
                .allMatch(user -> user.getStatus() == UserStatus.NON_ACTIVE);
    }
}
