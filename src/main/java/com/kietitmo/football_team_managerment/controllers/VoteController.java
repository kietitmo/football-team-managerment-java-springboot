package com.kietitmo.football_team_managerment.controllers;

import com.kietitmo.football_team_managerment.dto.request.VoteRequest;
import com.kietitmo.football_team_managerment.dto.response.ApiResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.VoteResponse;
import com.kietitmo.football_team_managerment.services.VoteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VoteController {
    VoteService voteService;

    @PostMapping
    ApiResponse<VoteResponse> create(@RequestBody VoteRequest request){
        return ApiResponse.<VoteResponse>builder()
                .result(voteService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<VoteResponse>> getAll(){
        return ApiResponse.<List<VoteResponse>>builder()
                .result(voteService.getAll())
                .build();
    }

    @PutMapping("/{vote}")
    ApiResponse<VoteResponse> update(@PathVariable String vote, @RequestBody VoteRequest request){
        return ApiResponse.<VoteResponse>builder()
                .result(voteService.updateVote(vote, request))
                .build();
    }

    @DeleteMapping("/{vote}")
    ApiResponse<Void> delete(@PathVariable String vote){
        voteService.delete(vote);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping(path = "/advance-search-with-specification")
    public ApiResponse<PageResponse<List<VoteResponse>>> advanceSearchWithSpecifications(Pageable pageable,
                                                                                         @RequestParam(required = false) String[] vote) {
        return ApiResponse.<PageResponse<List<VoteResponse>>>builder()
                .result(voteService.advanceSearchWithSpecifications(pageable, vote))
                .build();
    }
}