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
public class ContentResponse {
    String id;
    String title;
    String description;
    String eventId;
    Set<String> votesId;
    Date createdAt;
    Date updatedAt;
}