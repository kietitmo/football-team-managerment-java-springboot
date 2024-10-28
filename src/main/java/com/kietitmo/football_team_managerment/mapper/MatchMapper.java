package com.kietitmo.football_team_managerment.mapper;

import com.kietitmo.football_team_managerment.dto.request.MatchRequest;
import com.kietitmo.football_team_managerment.dto.response.EventParticipantResponse;
import com.kietitmo.football_team_managerment.dto.response.MatchResponse;
import com.kietitmo.football_team_managerment.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MatchMapper {
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "homeTeam", ignore = true)
    @Mapping(target = "awayTeam", ignore = true)
    Match toMatch(MatchRequest request);

    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipantToParticipantResponse")
    @Mapping(target = "homeTeamId", source = "homeTeam", qualifiedByName = "mapTeamToIds")
    @Mapping(target = "awayTeamId", source = "awayTeam", qualifiedByName = "mapTeamToIds")
    MatchResponse toMatchResponse(Match team);

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "homeTeam", ignore = true)
    @Mapping(target = "awayTeam", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMatch(@MappingTarget Match match, MatchRequest request);

    @Named("mapParticipantToParticipantResponse")
    default EventParticipantResponse mapParticipantToParticipantResponse(EventParticipant participant) {
        return EventParticipantResponse.builder()
                .id(participant.getId())
                .userId(participant.getUser().getId())
                .roleInEvent(participant.getRoleInEvent())
                .build();
    }

    @Named("mapTeamToIds")
    default String mapTeamToIds(Team team) {
        return team.getId();
    }

}