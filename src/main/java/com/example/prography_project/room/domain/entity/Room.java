package com.example.prography_project.room.domain.entity;

import com.example.prography_project.common.domain.BaseTimeEntity;
import com.example.prography_project.room.domain.RoomStatus;
import com.example.prography_project.room.domain.RoomType;
import com.example.prography_project.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Room extends BaseTimeEntity {
    @Id @Column(name = "room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host")
    private User host;

    @Enumerated(EnumType.STRING)
    private RoomType room_type;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    public Room(String title, User host, RoomType roomType, RoomStatus status) {
        this.title = title;
        this.host = host;
        this.room_type = roomType;
        this.status = status;
    }
}