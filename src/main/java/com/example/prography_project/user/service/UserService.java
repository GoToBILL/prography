package com.example.prography_project.user.service;

import com.example.prography_project.user.dto.UserListResponseDto;
import com.example.prography_project.user.dto.UserResponseDto;
import com.example.prography_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserListResponseDto getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponseDto> userPage = userRepository.findAllByOrderByIdAsc(pageable)
                .map(UserResponseDto::new);
        return new UserListResponseDto(userPage);
    }
}
