package com.example.prography_project.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    // 유저 관련 에러
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 회원"),
    USER_NOT_ACTIVE(BAD_REQUEST, "유저 상태가 활성 상태가 아닙니다."),
    USER_ALREADY_IN_ROOM(BAD_REQUEST, "이미 방에 참가 중입니다."),
    USER_NOT_IN_ROOM(BAD_REQUEST, "해당 방에 참가하지 않았습니다."),
    USER_NOT_HOST(BAD_REQUEST, "호스트만 게임을 시작할 수 있습니다."),
    USER_NOT_CHANGE_TEAM(BAD_REQUEST, "팀 변경이 불가능한 유저입니다."),

    ROOM_NOT_FOUND(NOT_FOUND, "존재하지 않는 방"),
    ROOM_FULL(BAD_REQUEST, "방의 정원이 가득 찼습니다."),
    ROOM_NOT_JOINABLE(BAD_REQUEST, "해당 방은 현재 참가할 수 없습니다."),
    ROOM_NOT_FULL(BAD_REQUEST, "방의 정원이 가득 차지 않았습니다."),
    ROOM_NOT_WAITING(BAD_REQUEST, "방이 대기 상태가 아닙니다."),


    // 게임 관련 에러
    GAME_ALREADY_STARTED(BAD_REQUEST, "게임이 이미 시작되었습니다."),
    GAME_NOT_READY(BAD_REQUEST, "게임을 시작할 수 있는 상태가 아닙니다."),

    // 공통 에러
    INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
    UNEXPECTED_ERROR(INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
