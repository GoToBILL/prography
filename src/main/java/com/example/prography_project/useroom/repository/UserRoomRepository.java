package com.example.prography_project.useroom.repository;

import com.example.prography_project.useroom.domain.TeamType;
import com.example.prography_project.useroom.domain.entity.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    boolean existsByUserId(Long userId);
    long countByRoomId(Long id);
    List<UserRoom> findAllByRoomId(Long id);
    void deleteByRoomId(Long id);
    Optional<UserRoom> findByUserIdAndRoomId(Long id, Long id1);
    long countByRoomIdAndTeam(Long id, TeamType newTeam);
}
