package com.example.prography_project.common.exception;


import com.example.prography_project.common.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ApiResponse<Void> handleBaseException(BaseException ex) {
        return ApiResponse.badRequest();
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGlobalException(Exception ex) {
        return ApiResponse.serverError();
    }
}
