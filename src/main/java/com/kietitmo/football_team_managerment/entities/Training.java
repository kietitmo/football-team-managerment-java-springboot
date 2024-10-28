package com.kietitmo.football_team_managerment.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "trainings")
@Entity
public class Training extends Event {
    Team team;
    Date startTime;
    Date endTime;
    String title;
    String description;
}
