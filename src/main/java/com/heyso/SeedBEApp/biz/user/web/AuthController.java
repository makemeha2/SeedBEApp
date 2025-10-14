package com.heyso.SeedBEApp.biz.user.web;

import com.heyso.SeedBEApp.biz.user.dao.UserMapper;
import com.heyso.SeedBEApp.biz.user.dto.LoginRequest;
import com.heyso.SeedBEApp.biz.user.dto.LoginResponse;
import com.heyso.SeedBEApp.biz.user.model.UserAccount;
import com.heyso.SeedBEApp.common.authentication.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
// 1) 사용자 조회
        UserAccount u = userMapper.findByUsername(req.getUsername());
        if (u == null || !"Y".equalsIgnoreCase(u.getUseYn())) {
            return ResponseEntity.status(401).build();
        }
// 2) 비밀번호 검증
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }
// 3) 권한 로드
        List<String> roles = userMapper.findRoles(u.getUserId());
// 4) 토큰 발급
        String token = tokenProvider.createAccessToken(u, roles);
        long expSec = 60L * 60L; // access-exp-min과 맞춰도 됨
        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expSec)
                .build());
    }
}
