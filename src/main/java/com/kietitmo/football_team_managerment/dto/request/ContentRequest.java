package com.kietitmo.football_team_managerment.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentRequest {
    String title;
    String description;
    String event;
    Set<String> votes;
}