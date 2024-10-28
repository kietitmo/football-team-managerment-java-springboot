package com.kietitmo.football_team_managerment.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoteRequest {
    String user;
    String content;
    int voteType;
    boolean valid;
}