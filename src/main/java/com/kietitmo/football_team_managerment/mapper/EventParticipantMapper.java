package com.kietitmo.football_team_managerment.mapper;

import com.kietitmo.football_team_managerment.dto.request.EventParticipantRequest;
import com.kietitmo.football_team_managerment.dto.response.EventParticipantResponse;
import com.kietitmo.football_team_managerment.entities.Event;
import com.kietitmo.football_team_managerment.entities.EventParticipant;
import com.kietitmo.football_team_managerment.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventParticipantMapper {
    @Mapping(target = "user", ignore = true)
    EventParticipant toEventParticipant(EventParticipantRequest request);

    @Mapping(target = "eventId", source = "event", qualifiedByName = "mapEventToIds")
    @Mapping(target = "userId", source = "user", qualifiedByName = "mapUserToIds")
    EventParticipantResponse toEventParticipantResponse(EventParticipant participant);

    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventParticipant(@MappingTarget EventParticipant participant, EventParticipantRequest request);

    @Named("mapEventToIds")
    default String mapEventToIds(Event event) {
        return event.getId();
    }

    @Named("mapUserToIds")
    default String mapUserToId(User user) {
        return user.getId();
    }
}