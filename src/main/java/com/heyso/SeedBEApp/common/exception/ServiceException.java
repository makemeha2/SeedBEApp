package com.heyso.SeedBEApp.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final String userMessage;

    public ServiceException(HttpStatus status, String code, String userMessage) {
        super(userMessage);
        this.status = status;
        this.code = code;
        this.userMessage = userMessage;
    }
}
