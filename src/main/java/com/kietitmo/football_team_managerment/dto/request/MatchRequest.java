package com.kietitmo.football_team_managerment.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchRequest {
    String homeTeam;
    String awayTeam;
    LocalDate matchDate;
    Integer homeGoals;
    Integer awayGoals;

    String title;
    String description;
    Set<EventParticipantRequest> participants;
    LocalDate creationDate;
    LocalDate lastUpdateDate;
}
