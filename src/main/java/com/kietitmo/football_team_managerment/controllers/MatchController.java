package com.kietitmo.football_team_managerment.controllers;


import com.kietitmo.football_team_managerment.dto.request.MatchRequest;
import com.kietitmo.football_team_managerment.dto.response.ApiResponse;
import com.kietitmo.football_team_managerment.dto.response.MatchResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.services.MatchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MatchController {
    MatchService matchService;

    @PostMapping
    ApiResponse<MatchResponse> create(@RequestBody MatchRequest request){
        return ApiResponse.<MatchResponse>builder()
                .result(matchService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<MatchResponse>> getAll(){
        return ApiResponse.<List<MatchResponse>>builder()
                .result(matchService.getAll())
                .build();
    }

    @PutMapping("/{match}")
    ApiResponse<MatchResponse> update(@PathVariable String match, @RequestBody MatchRequest request){
        return ApiResponse.<MatchResponse>builder()
                .result(matchService.updateMatch(match, request))
                .build();
    }

    @DeleteMapping("/{match}")
    ApiResponse<Void> delete(@PathVariable String match){
        matchService.delete(match);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping(path = "/advance-search-with-specification")
    public ApiResponse<PageResponse<List<MatchResponse>>> advanceSearchWithSpecifications(Pageable pageable,
                                                                                         @RequestParam(required = false) String[] match) {
        return ApiResponse.<PageResponse<List<MatchResponse>>>builder()
                .result(matchService.advanceSearchWithSpecifications(pageable, match))
                .build();
    }
}
