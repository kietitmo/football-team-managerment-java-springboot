package com.kietitmo.football_team_managerment.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "teams")
@Entity
public class Team extends AbstractEntity{
    String name;
    LocalDate creationDate;
    LocalDate lastUpdateDate;
    @ManyToOne
    User creator;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    Set<TeamMember> members = new HashSet<>();

    String logo;
}
