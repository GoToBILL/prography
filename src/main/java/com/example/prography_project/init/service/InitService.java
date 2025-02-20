package com.example.prography_project.init.service;

import com.example.prography_project.room.repository.RoomRepository;
import com.example.prography_project.user.domain.UserStatus;
import com.example.prography_project.user.domain.entity.User;
import com.example.prography_project.user.repository.UserRepository;
import com.example.prography_project.useroom.repository.UserRoomRepository;
import com.example.prography_project.util.FakerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InitService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final FakerApiClient fakerApiClient;

    public void initialize(int seed, int quantity) {
        userRoomRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();

        List<User> users = fakerApiClient.fetchUsers(seed, quantity);

        users.sort(Comparator.comparing(User::getFakerId));

        for (User user : users) {
            Long fakerId = user.getFakerId();

            if (fakerId <= 30) {
                user.setStatus(UserStatus.ACTIVE);
            } else if (fakerId <= 60) {
                user.setStatus(UserStatus.WAIT);
            } else {
                user.setStatus(UserStatus.NON_ACTIVE);
            }
        }

        userRepository.saveAll(users);
    }
}
