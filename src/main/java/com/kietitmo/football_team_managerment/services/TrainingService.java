package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.EventParticipantRequest;
import com.kietitmo.football_team_managerment.dto.request.TrainingRequest;
import com.kietitmo.football_team_managerment.dto.response.*;
import com.kietitmo.football_team_managerment.entities.*;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.exceptions.ErrorCode;
import com.kietitmo.football_team_managerment.mapper.TrainingMapper;
import com.kietitmo.football_team_managerment.repositories.EventParticipantRepository;
import com.kietitmo.football_team_managerment.repositories.TrainingRepository;
import com.kietitmo.football_team_managerment.repositories.TeamRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class TrainingService extends BaseService<Training, String, TrainingResponse, TrainingRepository> {
    TrainingRepository trainingRepository;
    EventParticipantRepository eventParticipantRepository;
    TeamRepository teamRepository;
    TrainingMapper trainingMapper;
    UserRepository userRepository;

    public TrainingResponse create(TrainingRequest request){
        var training = trainingMapper.toTraining(request);

        Optional<Team> teamOptional = teamRepository.findById(request.getTeamId());
        if (teamOptional.isEmpty()) {
            throw new AppException(ErrorCode.TEAM_NOT_EXISTED);
        }

        training.setTeam(teamOptional.get());

        Training savedTraining = trainingRepository.save(training);
        Set<EventParticipant> participants = new HashSet<>();
        for (EventParticipantRequest participantRequest : request.getParticipants()) {
            // Tìm kiếm User theo ID từ cơ sở dữ liệu
            Optional<User> userOptional = userRepository.findById(participantRequest.getUserId());

            if (userOptional.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_EXISTED);
            }


            User user = userOptional.get();

            // Tạo thực thể EventParticipant cho từng User
            EventParticipant participant = new EventParticipant();
            participant.setEvent(savedTraining); // Gán Training vào participant
            participant.setUser(user);
            participant.setRoleInEvent(participantRequest.getRoleInEvent()); // Gán vai trò

            participants.add(participant);
        }


        eventParticipantRepository.saveAll(participants);

        // Cập nhật tập hợp participants vào đối tượng Training và lưu lại
        savedTraining.setParticipants(participants);

        return trainingMapper.toTrainingResponse(trainingRepository.save(savedTraining));
    }

    public List<TrainingResponse> getAll(){
        return trainingRepository.findAll()
                .stream()
                .map(trainingMapper::toTrainingResponse)
                .toList();
    }

    public TrainingResponse updateTraining(String trainingId, TrainingRequest request) {
        Training savedTraining = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));

        trainingMapper.updateTraining(savedTraining, request);

        // Delete all current participants
        eventParticipantRepository.deleteAllById(savedTraining.getParticipants().stream().map(EventParticipant::getId).toList());

        Set<EventParticipant> participants = new HashSet<>();
        for (EventParticipantRequest participantRequest : request.getParticipants()) {
            // Tìm kiếm User theo ID từ cơ sở dữ liệu
            Optional<User> userOptional = userRepository.findById(participantRequest.getUserId());
            if (userOptional.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_EXISTED);
            }

            User user = userOptional.get();

            // Tạo thực thể EventParticipant cho từng User
            EventParticipant participant = new EventParticipant();
            participant.setEvent(savedTraining); // Gán Training vào participant
            participant.setUser(user);
            participant.setRoleInEvent(participantRequest.getRoleInEvent()); // Gán vai trò

            participants.add(participant);
        }


        eventParticipantRepository.saveAll(participants);

        // Cập nhật tập hợp participants vào đối tượng Training và lưu lại
        savedTraining.setParticipants(participants);

        return trainingMapper.toTrainingResponse(trainingRepository.save(savedTraining));
    }


    public void delete(String trainingId){
        trainingRepository.deleteById(trainingId);
    }

    @Override
    protected TrainingRepository getRepository() {
        return trainingRepository;
    }

    @Override
    protected PageResponse<List<TrainingResponse>> convertToPageResponse(Page<Training> items, Pageable pageable) {
        List<TrainingResponse> response = items.stream().map(item ->
             TrainingResponse.builder()
                    .id(item.getId())
                    .startTime(item.getStartTime())
                    .endTime(item.getEndTime())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .teamId(item.getTeam() != null ? item.getTeam().getId() : null)
                    .participants(item.getParticipants().stream().map(
                        participant -> {
                            return EventParticipantResponse.builder()
                                    .id(participant.getId())
                                    .userId(participant.getUser().getId())
                                    .roleInEvent(participant.getRoleInEvent())
                                    .build();
                        }
                    ).collect(Collectors.toSet()))
                    .createdAt(item.getCreatedAt())
                    .updatedAt(item.getUpdatedAt())
                    .build()
        ).toList();

        return PageResponse.<List<TrainingResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(items.getTotalPages())
                .totalItem(items.getTotalElements())
                .items(response)
                .build();
    }
}
