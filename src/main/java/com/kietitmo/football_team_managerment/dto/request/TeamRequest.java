package com.kietitmo.football_team_managerment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamRequest {
    String name;
    String creator;
    Set<MemberRequest> members;
}