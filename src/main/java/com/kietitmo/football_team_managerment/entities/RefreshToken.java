package com.kietitmo.football_team_managerment.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne()
    @JoinColumn(name = "user_id")  // Tên cột trong bảng refresh_token
    User user;

    @Column(length = 1024)
    String token;
}