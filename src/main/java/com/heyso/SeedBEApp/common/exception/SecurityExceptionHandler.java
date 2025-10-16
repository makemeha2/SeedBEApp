package com.heyso.SeedBEApp.common.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 이 핸들러가 없으면 401, 403 오류 발생시 공통예외처리기(GlobalExceptionHandler)를 통해
// 500번 Error로 처리된다.
// 따라서 이 핸들러가 GlobalExceptionHandler보다 높은 우선순위로 수행되도록 처리한다.

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SecurityExceptionHandler {

    // Spring Security 6 (@PreAuthorize)에서 사용
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        // 필요하면 로깅/DB 저장
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorBody(403, "Access Denied"));
    }

    // 다른 경로에서 오는 전통적인 AccessDenied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorBody(403, "Access Denied"));
    }

    // @ExceptionHandler(AuthenticationException.class) ...
    record ErrorBody(int status, String message) {}
}
