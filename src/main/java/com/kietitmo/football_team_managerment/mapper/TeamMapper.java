package com.kietitmo.football_team_managerment.mapper;

import com.kietitmo.football_team_managerment.dto.request.TeamRequest;
import com.kietitmo.football_team_managerment.dto.response.MemberResponse;
import com.kietitmo.football_team_managerment.dto.response.TeamResponse;
import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.Team;

import com.kietitmo.football_team_managerment.entities.TeamMember;
import com.kietitmo.football_team_managerment.entities.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface TeamMapper {
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "creator", ignore = true)
    Team toTeam(TeamRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "creator", ignore = true)
    void updateTeam(@MappingTarget Team team, TeamRequest request);

    @Mapping(target = "teamMembers", source = "members", qualifiedByName = "mapTeamMemberToMemberResponse")
    @Mapping(target = "creatorId", source = "creator", qualifiedByName = "mapCreatorToIds")
    TeamResponse toTeamResponse(Team team);

    @Named("mapTeamMemberToMemberResponse")
    default Set<MemberResponse> mapTeamMemberToMemberResponse(Set<TeamMember> teamMembers) {
        return teamMembers.stream().map(teamMember -> MemberResponse.builder()
                .roles(teamMember.getRoles().stream().map(Role::getName).toList())
                .userId(teamMember.getUser().getId())
                .teamMemberId(teamMember.getId())
                .build()).collect(Collectors.toSet());
    }

    @Named("mapCreatorToIds")
    default String mapCreatorToIds(User owner) {
        if (owner == null) {
            return null;
        }
        return owner.getId();
    }

}
