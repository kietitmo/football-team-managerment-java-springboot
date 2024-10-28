package com.kietitmo.football_team_managerment.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingRequest {
    String teamId;
    Date startTime;
    Date endTime;
    String title;
    String description;
    Set<EventParticipantRequest> participants;
}
