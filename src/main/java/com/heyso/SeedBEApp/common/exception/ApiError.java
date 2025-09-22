package com.heyso.SeedBEApp.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

// API 응답으로 전달할 예외정보 DTO
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(Instant timestamp,
                       String traceId,
                       int status,
                       String code,     // 애플리케이션 에러코드 (선택)
                       String message,  // 사용자에게 보여줄 메시지
                       String path) {
    public static ApiError of(String traceId, int status, String code, String message, String path) {
        return new ApiError(Instant.now(), traceId, status, code, message, path);
    }
}
