package com.example.prography_project.user.repository;

import com.example.prography_project.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findAllByOrderByIdAsc(Pageable pageable);

}