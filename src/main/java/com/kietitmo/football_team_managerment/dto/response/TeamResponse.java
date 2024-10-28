package com.kietitmo.football_team_managerment.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamResponse {
    String id;
    String name;
    String creatorId;
    Set<MemberResponse> teamMembers;
    Date createdAt;
    Date updatedAt;
    String logo;
}
