package com.kietitmo.football_team_managerment.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table() // Đổi tên bảng thành app_user
@Entity
public class Permission {
    @Id
    String name;
    String description;
}
