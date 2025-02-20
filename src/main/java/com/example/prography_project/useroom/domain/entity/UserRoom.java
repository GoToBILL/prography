package com.example.prography_project.useroom.domain.entity;

import com.example.prography_project.room.domain.entity.Room;
import com.example.prography_project.user.domain.entity.User;
import com.example.prography_project.useroom.domain.TeamType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UserRoom{
    @Id @Column(name = "userroom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TeamType team;

    public UserRoom(Room room, User user, TeamType team) {
        this.room = room;
        this.user = user;
        this.team = team;
    }
}