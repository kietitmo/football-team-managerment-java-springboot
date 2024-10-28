package com.kietitmo.football_team_managerment.controllers;

import com.kietitmo.football_team_managerment.dto.request.UserCreationRequest;
import com.kietitmo.football_team_managerment.dto.request.UserUpdateRequest;
import com.kietitmo.football_team_managerment.dto.response.ApiResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.UserResponse;
import com.kietitmo.football_team_managerment.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        return ApiResponse.<List<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all users successfully")
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/profile")
    ApiResponse<UserResponse> getProfile(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ApiResponse.<UserResponse>builder()
                .result(userService.getProfile(username))
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) throws IllegalAccessException {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    @GetMapping("/role")
    ApiResponse<String> getRole(){
        return ApiResponse.<String>builder()
                .result(userService.testRole())
                .build();
    }

    @GetMapping("/per")
    ApiResponse<String> getPermission(){
        return ApiResponse.<String>builder()
                .result(userService.testPermission())
                .build();
    }

    @GetMapping(path = "/advance-search-with-specification")
    public ApiResponse<PageResponse<List<UserResponse>>> advanceSearchWithSpecifications(Pageable pageable,
                                                          @RequestParam(required = false) String[] user) {
        return ApiResponse.<PageResponse<List<UserResponse>>>builder()
                .result(userService.advanceSearchWithSpecifications(pageable, user))
                .build();
    }

    @PostMapping("/{userId}/upload-avatar")
    public ApiResponse<UserResponse> uploadUserAvatar(@PathVariable String userId,
                                                   @RequestParam("avatar") MultipartFile avatarFile) throws IOException {
            return ApiResponse.<UserResponse>builder()
                    .result(userService.uploadUserAvatar(userId, avatarFile))
                    .build();

    }
}