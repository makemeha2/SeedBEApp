package com.heyso.SeedBEApp.common.exception;

import com.heyso.SeedBEApp.common.web.TraceIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ExceptionLogService exceptionLogService;

    // 애플리케이션 커스텀 예외가 있다면 이렇게:
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiError> handleService(ServiceException ex, HttpServletRequest req) {
        return buildAndPersist(ex, req, ex.getStatus(), ex.getCode(), ex.getUserMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .orElse("Invalid request");
        return buildAndPersist(ex, req, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", msg);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return buildAndPersist(ex, req, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE",
                "지원하지 않는 Content-Type 입니다.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethod(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return buildAndPersist(ex, req, HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                "허용되지 않은 HTTP 메서드입니다.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleUpload(MaxUploadSizeExceededException ex, HttpServletRequest req) {
        return buildAndPersist(ex, req, HttpStatus.PAYLOAD_TOO_LARGE, "UPLOAD_TOO_LARGE",
                "업로드 가능한 최대 용량을 초과했습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        return buildAndPersist(ex, req, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "서버 내부 오류가 발생했습니다.");
    }

    private ResponseEntity<ApiError> buildAndPersist(Throwable ex,
                                                     HttpServletRequest req,
                                                     HttpStatus status,
                                                     String code,
                                                     String userMessage) {
        String traceId = (String) req.getAttribute(TraceIdFilter.TRACE_ID);
        ApiError body = ApiError.of(traceId, status.value(), code, userMessage, req.getRequestURI());

        // 요청 본문을 한 번 더 읽기 어려우니, 가능하면 ContentCachingRequestWrapper로 감싸거나
        // 여기서는 best-effort 로 시도
        String requestBody = null;
        try {
            requestBody = StreamUtils.copyToString(req.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ignored) { }

        exceptionLogService.saveAsync(ex, status.value(), code, userMessage, req, traceId, requestBody);

        return ResponseEntity.status(status).body(body);
    }
}
