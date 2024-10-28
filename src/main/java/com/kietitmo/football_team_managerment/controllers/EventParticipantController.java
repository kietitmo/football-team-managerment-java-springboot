package com.kietitmo.football_team_managerment.controllers;

import com.kietitmo.football_team_managerment.dto.request.EventParticipantRequest;
import com.kietitmo.football_team_managerment.dto.response.ApiResponse;
import com.kietitmo.football_team_managerment.dto.response.EventParticipantResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.services.EventParticipantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventParticipants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventParticipantController {
    EventParticipantService eventParticipantService;

    @PostMapping
    ApiResponse<EventParticipantResponse> create(@RequestBody EventParticipantRequest request){
        return ApiResponse.<EventParticipantResponse>builder()
                .result(eventParticipantService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<EventParticipantResponse>> getAll(){
        return ApiResponse.<List<EventParticipantResponse>>builder()
                .result(eventParticipantService.getAll())
                .build();
    }

    @PutMapping("/{participant}")
    ApiResponse<EventParticipantResponse> update(@PathVariable String participant, @RequestBody EventParticipantRequest request){
        return ApiResponse.<EventParticipantResponse>builder()
                .result(eventParticipantService.updateEventParticipant(participant, request))
                .build();
    }

    @DeleteMapping("/{participant}")
    ApiResponse<Void> delete(@PathVariable String participant){
        eventParticipantService.delete(participant);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping(path = "/advance-search-with-specification")
    public ApiResponse<PageResponse<List<EventParticipantResponse>>> advanceSearchWithSpecifications(Pageable pageable,
                                                                                         @RequestParam(required = false) String[] participant) {
        return ApiResponse.<PageResponse<List<EventParticipantResponse>>>builder()
                .result(eventParticipantService.advanceSearchWithSpecifications(pageable, participant))
                .build();
    }
}