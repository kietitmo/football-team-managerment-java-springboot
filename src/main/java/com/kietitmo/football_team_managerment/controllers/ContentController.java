package com.kietitmo.football_team_managerment.controllers;

import com.kietitmo.football_team_managerment.dto.request.ContentRequest;
import com.kietitmo.football_team_managerment.dto.response.ApiResponse;
import com.kietitmo.football_team_managerment.dto.response.ContentResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.services.ContentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ContentController {
    ContentService contentService;

    @PostMapping
    ApiResponse<ContentResponse> create(@RequestBody ContentRequest request){
        return ApiResponse.<ContentResponse>builder()
                .result(contentService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ContentResponse>> getAll(){
        return ApiResponse.<List<ContentResponse>>builder()
                .result(contentService.getAll())
                .build();
    }

    @PutMapping("/{content}")
    ApiResponse<ContentResponse> update(@PathVariable String content, @RequestBody ContentRequest request){
        return ApiResponse.<ContentResponse>builder()
                .result(contentService.updateContent(content, request))
                .build();
    }

    @DeleteMapping("/{content}")
    ApiResponse<Void> delete(@PathVariable String content){
        contentService.delete(content);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping(path = "/advance-search-with-specification")
    public ApiResponse<PageResponse<List<ContentResponse>>> advanceSearchWithSpecifications(Pageable pageable,
                                                                                                     @RequestParam(required = false) String[] content) {
        return ApiResponse.<PageResponse<List<ContentResponse>>>builder()
                .result(contentService.advanceSearchWithSpecifications(pageable, content))
                .build();
    }
}