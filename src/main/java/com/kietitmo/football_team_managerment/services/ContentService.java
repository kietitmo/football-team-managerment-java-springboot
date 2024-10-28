package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.ContentRequest;
import com.kietitmo.football_team_managerment.dto.response.ContentResponse;
import com.kietitmo.football_team_managerment.dto.response.EventParticipantResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.VoteResponse;
import com.kietitmo.football_team_managerment.entities.AbstractEntity;
import com.kietitmo.football_team_managerment.entities.Content;
import com.kietitmo.football_team_managerment.entities.EventParticipant;
import com.kietitmo.football_team_managerment.entities.Vote;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.exceptions.ErrorCode;
import com.kietitmo.football_team_managerment.mapper.ContentMapper;
import com.kietitmo.football_team_managerment.repositories.ContentRepository;
import com.kietitmo.football_team_managerment.repositories.EventParticipantRepository;
import com.kietitmo.football_team_managerment.repositories.EventRepository;
import com.kietitmo.football_team_managerment.repositories.VoteRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@PreAuthorize("hasRole('ADMIN')")
public class ContentService extends BaseService<Content, String, ContentResponse, ContentRepository>{
    EventRepository eventRepository;
    VoteRepository voteRepository;
    ContentRepository contentRepository;
    ContentMapper contentMapper;

    public ContentResponse create(ContentRequest request) {
        var content =  contentMapper.toContent(request);

        var event = eventRepository.findById(request.getEvent())
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_EXISTED));
        var votes = voteRepository.findAllById(request.getVotes());
        Set<Vote> votesSet = new HashSet<>(votes);

        content.setEvent(event);
        content.setVotes(votesSet);

        contentRepository.save(content);
        return contentMapper.toContentResponse(content);
    }

    public List<ContentResponse> getAll(){
        return contentRepository.findAll()
                .stream()
                .map(contentMapper::toContentResponse)
                .toList();
    }

    public ContentResponse updateContent(String contentId, ContentRequest request) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTENT_NOT_EXISTED));

        contentMapper.updateContent(content, request);

        var event = eventRepository.findById(request.getEvent())
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_EXISTED));
        var votes = voteRepository.findAllById(request.getVotes());
        Set<Vote> votesSet = new HashSet<>(votes);

        content.setEvent(event);
        content.setVotes(votesSet);

        contentRepository.save(content);
        return contentMapper.toContentResponse(content);
    }


    public void delete(String contentId){
        contentRepository.deleteById(contentId);
    }

    @Override
    protected ContentRepository getRepository() {
        return contentRepository;
    }

    @Override
    protected PageResponse<List<ContentResponse>> convertToPageResponse(Page<Content> items, Pageable pageable) {
        List<ContentResponse> response = items.stream()
                .map(item -> ContentResponse.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .eventId(item.getId())
                        .votesId(item.getVotes().stream().map(Vote::getId).collect(Collectors.toSet()))
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build())
                .toList();

        return PageResponse.<List<ContentResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(items.getTotalPages())
                .totalItem(items.getTotalElements())
                .items(response)
                .build();
    }
}
