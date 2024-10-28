package com.kietitmo.football_team_managerment.mapper;

import com.kietitmo.football_team_managerment.dto.request.VoteRequest;
import com.kietitmo.football_team_managerment.dto.response.VoteResponse;
import com.kietitmo.football_team_managerment.entities.Content;
import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.User;
import com.kietitmo.football_team_managerment.entities.Vote;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VoteMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "content", ignore = true)
    Vote toVote(VoteRequest request);

    @Mapping(target = "userId", source = "user", qualifiedByName = "mapUserToIds")
    @Mapping(target = "contentId", source = "content", qualifiedByName = "mapContentToIds")
    VoteResponse toVoteResponse(Vote vote);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "content", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVote(@MappingTarget Vote vote, VoteRequest request);

    @Named("mapUserToIds")
    default String mapUserToId(User user) {
        return user.getId();
    }

    @Named("mapContentToIds")
    default String mapContentToIds(Content content) {
        return content.getId();
    }
}