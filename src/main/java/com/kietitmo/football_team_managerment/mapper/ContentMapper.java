package com.kietitmo.football_team_managerment.mapper;

import com.kietitmo.football_team_managerment.dto.request.ContentRequest;
import com.kietitmo.football_team_managerment.dto.request.VoteRequest;
import com.kietitmo.football_team_managerment.dto.response.ContentResponse;
import com.kietitmo.football_team_managerment.dto.response.VoteResponse;
import com.kietitmo.football_team_managerment.entities.Content;
import com.kietitmo.football_team_managerment.entities.Event;
import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.Vote;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    @Mapping(target = "votes", ignore = true)
    @Mapping(target = "event", ignore = true)
    Content toContent(ContentRequest request);

    @Mapping(target = "eventId", source = "event", qualifiedByName = "mapEventToIds")
    @Mapping(target = "votesId", source = "votes", qualifiedByName = "mapVotesToIds")
    ContentResponse toContentResponse(Content content);

    @Mapping(target = "event", ignore = true)
    @Mapping(target = "votes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateContent(@MappingTarget Content content, ContentRequest request);

    @Named("mapVotesToIds")
    default Set<String> mapVotesToIds(Set<Vote> votes) {
        return votes.stream().map(Vote::getId).collect(Collectors.toSet());
    }

    @Named("mapEventToIds")
    default String mapEventToIds(Event event) {
        return event.getId();
    }
}