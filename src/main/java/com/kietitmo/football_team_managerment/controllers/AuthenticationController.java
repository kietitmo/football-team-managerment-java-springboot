package com.kietitmo.football_team_managerment.controllers;

import com.kietitmo.football_team_managerment.dto.request.AuthenticationRequest;
import com.kietitmo.football_team_managerment.dto.response.ApiResponse;
import com.kietitmo.football_team_managerment.dto.response.AuthenticationResponse;
import com.kietitmo.football_team_managerment.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        AuthenticationResponse result = authenticationService.authenticate(request);
        System.out.println("==============" + request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login successfully!")
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }

//    @PostMapping("/introspect")
//    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
//        IntrospectResponse result = authenticationService.introspect(request);
//        return ApiResponse.<IntrospectResponse>builder()
//                .message("Introspect tokens successfully!")
//                .code(HttpStatus.OK.value())
//                .result(result)
//                .build();
//    }

    @PostMapping("/logout")
    ApiResponse<Void>logout(HttpServletRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .message("Logout successfully!")
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(HttpServletRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("refresh token successfully!")
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }
}