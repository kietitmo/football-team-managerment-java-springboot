package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.VoteRequest;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.UserResponse;
import com.kietitmo.football_team_managerment.dto.response.VoteResponse;
import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.Vote;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.exceptions.ErrorCode;
import com.kietitmo.football_team_managerment.mapper.VoteMapper;
import com.kietitmo.football_team_managerment.repositories.ContentRepository;
import com.kietitmo.football_team_managerment.repositories.VoteRepository;
import com.kietitmo.football_team_managerment.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class VoteService extends BaseService<Vote, String, VoteResponse, VoteRepository>{
    UserRepository userRepository;
    ContentRepository contentRepository;
    VoteMapper voteMapper;
    VoteRepository voteRepository;

    public VoteResponse create(VoteRequest request) {
        var vote =  voteMapper.toVote(request);

        var user = userRepository.findById(request.getUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var content = contentRepository.findById(request.getContent())
                .orElseThrow(() -> new AppException(ErrorCode.CONTENT_NOT_EXISTED));

        // Check vote and content are unique?
        Optional<Vote> existingVote = voteRepository.findByUserAndContent(user, content);
        if (existingVote.isPresent()) {
            throw new AppException(ErrorCode.DUPLICATED_VOTE);
        }

        vote.setUser(user);
        vote.setContent(content);
        return voteMapper.toVoteResponse(voteRepository.save(vote));
    }

    public List<VoteResponse> getAll(){
        return voteRepository.findAll()
                .stream()
                .map(voteMapper::toVoteResponse)
                .toList();
    }

    public VoteResponse updateVote(String voteId, VoteRequest request) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));
        voteMapper.updateVote(vote, request);

        var content = contentRepository.findById(request.getContent())
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_EXISTED));
        var user = userRepository.findById(request.getUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        vote.setContent(content);
        vote.setUser(user);

        return voteMapper.toVoteResponse(voteRepository.save(vote));
    }


    public void delete(String voteId){
        voteRepository.deleteById(voteId);
    }

    @Override
    protected VoteRepository getRepository() {
        return voteRepository;
    }

    @Override
    protected PageResponse<List<VoteResponse>> convertToPageResponse(Page<Vote> items, Pageable pageable) {
        List<VoteResponse> response = items.stream().map(item -> VoteResponse.builder()
                .id(item.getId())
                .contentId(item.getContent().getId())
                .userId(item.getUser().getId())
                .voteType(item.getVoteType())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build()).toList();

        return PageResponse.<List<VoteResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(items.getTotalPages())
                .totalItem(items.getTotalElements())
                .items(response)
                .build();
    }
}
