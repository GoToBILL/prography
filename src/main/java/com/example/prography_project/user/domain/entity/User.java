package com.example.prography_project.user.domain.entity;

import com.example.prography_project.common.domain.BaseTimeEntity;
import com.example.prography_project.user.domain.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer fakerId;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public User(Integer fakerId, String name, String email) {
        this.fakerId = fakerId;
        this.name = name;
        this.email = email;
    }
}