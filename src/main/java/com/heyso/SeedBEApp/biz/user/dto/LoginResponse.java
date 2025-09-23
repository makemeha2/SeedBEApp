package com.heyso.SeedBEApp.biz.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType; // "Bearer"
    private long expiresIn; // seconds
}