package com.example.prography_project.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T result;

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.", result);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.",null);
    }

    public static <T> ApiResponse<T> badRequest() {
        return new ApiResponse<>(201, "불가능한 요청입니다.", null);
    }

    public static <T> ApiResponse<T> serverError() {
        return new ApiResponse<>(500, "에러가 발생했습니다.", null);
    }
}