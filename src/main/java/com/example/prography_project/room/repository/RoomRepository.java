package com.example.prography_project.room.repository;

import com.example.prography_project.room.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    Page<Room> findAllByOrderByIdAsc(Pageable pageable);
}
