package com.kietitmo.football_team_managerment.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "matches")
@Entity
public class Match extends Event {
    @ManyToOne
    Team homeTeam;
    @ManyToOne
    Team awayTeam;

    String title;
    String description;
    LocalDate matchDate;
    Integer homeGoals;
    Integer awayGoals;
}
