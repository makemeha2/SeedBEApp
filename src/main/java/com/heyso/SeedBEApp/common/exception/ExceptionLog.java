package com.heyso.SeedBEApp.common.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

// DB에 저장하기 위한 예외정보 Model
@Data
@Builder
public class ExceptionLog {
    private Long id;
    private LocalDateTime occurredAt;
    private String traceId;
    private Integer status;
    private String errorCode;
    private String message;
    private String path;
    private String httpMethod;
    private String queryString;
    private String headersJson;
    private String bodyText;
    private String stacktrace;
}
