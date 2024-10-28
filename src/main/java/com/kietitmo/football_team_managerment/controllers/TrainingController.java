package com.kietitmo.football_team_managerment.controllers;


import com.kietitmo.football_team_managerment.dto.request.TrainingRequest;
import com.kietitmo.football_team_managerment.dto.response.ApiResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.TrainingResponse;
import com.kietitmo.football_team_managerment.services.TrainingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TrainingController {
    TrainingService trainingService;

    @PostMapping
    ApiResponse<TrainingResponse> create(@RequestBody TrainingRequest request){
        return ApiResponse.<TrainingResponse>builder()
                .result(trainingService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<TrainingResponse>> getAll(){
        return ApiResponse.<List<TrainingResponse>>builder()
                .result(trainingService.getAll())
                .build();
    }

    @PutMapping("/{training}")
    ApiResponse<TrainingResponse> update(@PathVariable String training, @RequestBody TrainingRequest request){
        return ApiResponse.<TrainingResponse>builder()
                .result(trainingService.updateTraining(training, request))
                .build();
    }

    @DeleteMapping("/{training}")
    ApiResponse<Void> delete(@PathVariable String training){
        trainingService.delete(training);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping(path = "/advance-search-with-specification")
    public ApiResponse<PageResponse<List<TrainingResponse>>> advanceSearchWithSpecifications(Pageable pageable,
                                                                                         @RequestParam(required = false) String[] training) {
        return ApiResponse.<PageResponse<List<TrainingResponse>>>builder()
                .result(trainingService.advanceSearchWithSpecifications(pageable, training))
                .build();
    }
}
