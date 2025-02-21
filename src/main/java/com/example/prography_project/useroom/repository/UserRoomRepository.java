package com.example.prography_project.useroom.repository;

import com.example.prography_project.useroom.domain.TeamType;
import com.example.prography_project.useroom.domain.entity.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {
    boolean existsByUserId(Integer userId);
    long countByRoomId(Integer id);
    List<UserRoom> findAllByRoomId(Integer id);
    void deleteByRoomId(Integer id);
    Optional<UserRoom> findByUserIdAndRoomId(Integer id, Integer id1);
    long countByRoomIdAndTeam(Integer id, TeamType newTeam);
}
