package com.kietitmo.football_team_managerment.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),

    TEAM_NOT_EXISTED(1009, "Team not existed", HttpStatus.NOT_FOUND),
    MATCH_NOT_EXISTED(1010, "Match not existed", HttpStatus.NOT_FOUND),
    EVENT_NOT_EXISTED(1011, "Event not existed", HttpStatus.NOT_FOUND),
    TOKEN_NOT_EXISTED(1012, "Token not existed", HttpStatus.NOT_FOUND),
    CONTENT_NOT_EXISTED(1012, "Content not existed", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(1014, "Role not existed", HttpStatus.NOT_FOUND),
    DUPLICATED_VOTE(1013, "Vote already exists for this user and content", HttpStatus.BAD_REQUEST),

    INVALID_KEY_SEARCH(1014, "Invalid searching key", HttpStatus.BAD_REQUEST),

    ERROR_UPLOADED_AVATAR(1014, "Error when uploading avatar", HttpStatus.BAD_REQUEST),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    int code;
    String message;
    HttpStatusCode statusCode;
}