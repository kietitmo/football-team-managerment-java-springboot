package com.kietitmo.football_team_managerment.controllers;

import com.kietitmo.football_team_managerment.dto.request.TeamRequest;
import com.kietitmo.football_team_managerment.dto.response.ApiResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.TeamResponse;
import com.kietitmo.football_team_managerment.services.TeamService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TeamController {
    TeamService teamService;

    @PostMapping
    ApiResponse<TeamResponse> create(@RequestBody TeamRequest request){
        return ApiResponse.<TeamResponse>builder()
                .result(teamService.createTeam(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<TeamResponse>> getAll(){
        return ApiResponse.<List<TeamResponse>>builder()
                .result(teamService.getAll())
                .build();
    }

    @GetMapping("/{team}")
    ApiResponse<TeamResponse> getTeam(@PathVariable String team){
        return ApiResponse.<TeamResponse>builder()
                .result(teamService.getTeam(team))
                .build();
    }

    @PutMapping("/{team}")
    ApiResponse<TeamResponse> update(@PathVariable String team, @RequestBody TeamRequest request){
        return ApiResponse.<TeamResponse>builder()
                .result(teamService.updateTeam(team, request))
                .build();
    }

    @DeleteMapping("/{team}")
    ApiResponse<Void> delete(@PathVariable String team){
        teamService.deleteTeam(team);
        return ApiResponse.<Void>builder()
                .build();
    }

    @GetMapping(path = "/advance-search-with-specification")
    public ApiResponse<PageResponse<List<TeamResponse>>> advanceSearchWithSpecifications(Pageable pageable,
                                                                                         @RequestParam(required = false) String[] team) {
        return ApiResponse.<PageResponse<List<TeamResponse>>>builder()
                .result(teamService.advanceSearchWithSpecifications(pageable, team))
                .build();
    }

    @PostMapping("/{teamId}/upload-logo")
    public ApiResponse<TeamResponse> uploadUserAvatar(@PathVariable String teamId,
                                                      @RequestParam("logo") MultipartFile avatarFile) throws IOException {
        return ApiResponse.<TeamResponse>builder()
                .result(teamService.uploadteamLogo(teamId, avatarFile))
                .build();

    }
}