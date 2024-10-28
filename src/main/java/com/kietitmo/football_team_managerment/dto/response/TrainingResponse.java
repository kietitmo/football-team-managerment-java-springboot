package com.kietitmo.football_team_managerment.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingResponse {
    String id;
    String teamId;
    Date startTime;
    Date endTime;
    String title;
    String description;
    Set<EventParticipantResponse> participants;
    Date createdAt;
    Date updatedAt;
}
