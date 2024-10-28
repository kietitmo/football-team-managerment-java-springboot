package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.EventParticipantRequest;
import com.kietitmo.football_team_managerment.dto.response.EventParticipantResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.entities.EventParticipant;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.exceptions.ErrorCode;
import com.kietitmo.football_team_managerment.mapper.EventParticipantMapper;
import com.kietitmo.football_team_managerment.repositories.EventParticipantRepository;
import com.kietitmo.football_team_managerment.repositories.EventRepository;
import com.kietitmo.football_team_managerment.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
@Transactional
public class EventParticipantService extends BaseService<EventParticipant, String, EventParticipantResponse, EventParticipantRepository>{
    EventParticipantRepository eventParticipantRepository;
    EventParticipantMapper eventParticipantMapper;
    UserRepository userRepository;
    EventRepository eventRepository;

    public EventParticipantResponse create(EventParticipantRequest request) {
        var participant =  eventParticipantMapper.toEventParticipant(request);
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_EXISTED));

        participant.setUser(user);
        participant.setEvent(event);

        return eventParticipantMapper.toEventParticipantResponse(eventParticipantRepository.save(participant));
    }

    public List<EventParticipantResponse> getAll(){
        return eventParticipantRepository.findAll()
                .stream()
                .map(eventParticipantMapper::toEventParticipantResponse)
                .toList();
    }

    public EventParticipantResponse updateEventParticipant(String eventParticipantId, EventParticipantRequest request) {
        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantId)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));

        eventParticipantMapper.updateEventParticipant(eventParticipant, request);

        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        eventParticipant.setUser(user);

        return eventParticipantMapper.toEventParticipantResponse(eventParticipantRepository.save(eventParticipant));
    }


    public void delete(String eventParticipantId){
        eventParticipantRepository.deleteById(eventParticipantId);
    }

    @Override
    protected EventParticipantRepository getRepository() {
        return eventParticipantRepository;
    }

    @Override
    protected PageResponse<List<EventParticipantResponse>> convertToPageResponse(Page<EventParticipant> items, Pageable pageable) {
        List<EventParticipantResponse> response = items.stream()
                .map(item -> EventParticipantResponse.builder()
                        .id(item.getId())
                        .userId(item.getUser().getId())
                        .roleInEvent(item.getRoleInEvent())
                        .eventId(item.getId())
                        .build())
                .toList();

        return PageResponse.<List<EventParticipantResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(items.getTotalPages())
                .totalItem(items.getTotalElements())
                .items(response)
                .build();
    }
}
