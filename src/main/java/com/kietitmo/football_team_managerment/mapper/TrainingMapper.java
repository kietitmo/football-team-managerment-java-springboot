package com.kietitmo.football_team_managerment.mapper;

import com.kietitmo.football_team_managerment.dto.request.TrainingRequest;
import com.kietitmo.football_team_managerment.dto.response.EventParticipantResponse;
import com.kietitmo.football_team_managerment.dto.response.TrainingResponse;
import com.kietitmo.football_team_managerment.entities.EventParticipant;
import com.kietitmo.football_team_managerment.entities.Team;
import com.kietitmo.football_team_managerment.entities.Training;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "team", ignore = true)
    Training toTraining(TrainingRequest request);

    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipantToParticipantResponse")
    @Mapping(target = "teamId", source = "team", qualifiedByName = "mapTeamToIds")
    TrainingResponse toTrainingResponse(Training training);

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTraining(@MappingTarget Training training, TrainingRequest request);

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