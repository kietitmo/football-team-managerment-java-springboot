package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.EventParticipantRequest;
import com.kietitmo.football_team_managerment.dto.request.MatchRequest;
import com.kietitmo.football_team_managerment.dto.response.*;
import com.kietitmo.football_team_managerment.entities.*;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.exceptions.ErrorCode;
import com.kietitmo.football_team_managerment.mapper.MatchMapper;
import com.kietitmo.football_team_managerment.repositories.EventParticipantRepository;
import com.kietitmo.football_team_managerment.repositories.MatchRepository;
import com.kietitmo.football_team_managerment.repositories.TeamRepository;
import com.kietitmo.football_team_managerment.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
@Transactional
public class MatchService extends BaseService<Match, String, MatchResponse, MatchRepository> {
    MatchRepository matchRepository;
    EventParticipantRepository eventParticipantRepository;
    TeamRepository teamRepository;
    MatchMapper matchMapper;
    UserRepository userRepository;

    public MatchResponse create(MatchRequest request){
        var match = matchMapper.toMatch(request);

        var homeTeam = teamRepository.findById(request.getHomeTeam())
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_EXISTED));
        var awayTeam = teamRepository.findById(request.getAwayTeam())
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_EXISTED));

        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);

        Match savedMatch = matchRepository.save(match);

        Set<EventParticipant> participants = new HashSet<>();
        for (EventParticipantRequest participantRequest : request.getParticipants()) {
            Optional<User> userOptional = userRepository.findById(participantRequest.getUserId());
            if (userOptional.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_EXISTED);
            }

            User user = userOptional.get();

            EventParticipant participant = new EventParticipant();
            participant.setEvent(savedMatch);
            participant.setUser(user);
            participant.setRoleInEvent(participantRequest.getRoleInEvent());

            participants.add(participant);
        }

        eventParticipantRepository.saveAll(participants);

        savedMatch.setParticipants(participants);

        return matchMapper.toMatchResponse(matchRepository.save(savedMatch));
    }

    public List<MatchResponse> getAll(){
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::toMatchResponse)
                .toList();
    }

    public MatchResponse updateMatch(String matchId, MatchRequest request) {
        Match savedMatch = matchRepository.findById(matchId)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));
        matchMapper.updateMatch(savedMatch, request);

        // Delete all current participant
        eventParticipantRepository.deleteAllById(savedMatch.getParticipants().stream().map(EventParticipant::getId).toList());

        Set<EventParticipant> participants = new HashSet<>();
        for (EventParticipantRequest participantRequest : request.getParticipants()) {
            Optional<User> userOptional = userRepository.findById(participantRequest.getUserId());
            if (userOptional.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_EXISTED);
            }

            User user = userOptional.get();

            EventParticipant participant = new EventParticipant();
            participant.setEvent(savedMatch);
            participant.setUser(user);
            participant.setRoleInEvent(participantRequest.getRoleInEvent());

            participants.add(participant);
        }

        eventParticipantRepository.saveAll(participants);

        savedMatch.setParticipants(participants);

        return matchMapper.toMatchResponse(matchRepository.save(savedMatch));
    }


    public void delete(String matchId){
        matchRepository.deleteById(matchId);
    }

    @Override
    protected MatchRepository getRepository() {
        return matchRepository;
    }


    @Override
    protected PageResponse<List<MatchResponse>> convertToPageResponse(Page<Match> matches, Pageable pageable) {
        List<MatchResponse> response = matches.stream().map(match -> MatchResponse.builder()
                .id(match.getId())
                .homeTeamId(match.getHomeTeam().getId())
                .awayTeamId(match.getAwayTeam().getId())
                .homeGoals(match.getHomeGoals())
                .awayGoals(match.getAwayGoals())
                .matchDate(match.getMatchDate())
                .title(match.getTitle())
                .description(match.getDescription())
                .createdAt(match.getCreatedAt())
                .updatedAt(match.getUpdatedAt())
                .participants(match.getParticipants().stream().map(
                        participant ->
                             EventParticipantResponse.builder()
                                    .id(participant.getId())
                                    .userId(participant.getUser().getId())
                                    .roleInEvent(participant.getRoleInEvent())
                                    .build()
                ).collect(Collectors.toSet()))
                .build()).toList();

        return PageResponse.<List<MatchResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(matches.getTotalPages())
                .totalItem(matches.getTotalElements())
                .items(response)
                .build();
    }
}