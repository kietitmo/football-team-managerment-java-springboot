package com.kietitmo.football_team_managerment.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "app_user")
@Entity
public class User extends AbstractEntity{
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<TeamMember> teams = new HashSet<>();

    @ManyToMany
    Set<Role> roles;
    String avatar; // Lưu trữ đường dẫn của avatar

}
