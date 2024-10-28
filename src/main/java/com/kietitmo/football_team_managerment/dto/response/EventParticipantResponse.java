package com.kietitmo.football_team_managerment.dto.response;

import com.kietitmo.football_team_managerment.enums.RoleInEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventParticipantResponse {
    String id;
    String userId;
    String eventId;
    RoleInEvent roleInEvent;
}
