package com.example.prography_project.user.service;

import com.example.prography_project.user.domain.UserStatus;
import com.example.prography_project.user.domain.entity.User;
import com.example.prography_project.user.dto.UserListResponseDto;
import com.example.prography_project.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void 오름차순으로_잘_유저리스트를_뽑아주느냐() {
        // Given: fakerId 순서대로 저장 (정렬 후 저장)
        List<User> users = Arrays.asList(
                new User(3, "Charlie", "charlie@example.com"),
                new User(1, "Alice", "alice@example.com"),
                new User(5, "Eve", "eve@example.com"),
                new User(4, "David", "david@example.com"),
                new User(2, "Bob", "bob@example.com")
        );
        users.get(0).setStatus(UserStatus.ACTIVE);
        users.get(1).setStatus(UserStatus.ACTIVE);
        users.get(2).setStatus(UserStatus.ACTIVE);
        users.get(3).setStatus(UserStatus.ACTIVE);
        users.get(4).setStatus(UserStatus.ACTIVE);

        // fakerId 기준으로 정렬하여 저장
        users.sort(Comparator.comparing(User::getFakerId));
        userRepository.saveAll(users);

        // 저장된 유저를 다시 불러와서 ID 확인 (Repository에서 자동 생성된 ID 기준)
        List<User> savedUsers = userRepository.findAll(Sort.by("id").ascending());

        // When: userService 실행 (Repository에서 id 기준으로 정렬된 데이터 가져옴)
        UserListResponseDto result = userService.getAllUsers(0, 5);

        // Then: 결과 검증
        assertThat(result.getUserList()).hasSize(5);

        // ID가 오름차순으로 정렬되었는지 확인
        for (int i = 0; i < savedUsers.size(); i++) {
            assertThat(result.getUserList().get(i).getId()).isEqualTo(savedUsers.get(i).getId());
        }

        // 정렬 검증
        assertThat(result.getUserList()).isSortedAccordingTo(Comparator.comparingLong(u -> u.getId()));
    }
}
