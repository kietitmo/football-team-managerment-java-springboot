package com.kietitmo.football_team_managerment.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoteResponse {
    String id;
    String userId;
    String contentId;
    int voteType;
    boolean valid;
    Date createdAt;
    Date updatedAt;
}