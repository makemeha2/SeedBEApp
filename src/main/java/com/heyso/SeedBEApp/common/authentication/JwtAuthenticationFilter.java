package com.heyso.SeedBEApp.common.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;


    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Jws<Claims> jws = tokenProvider.parse(token);
                Claims claims = jws.getBody();

                Long userId = tokenProvider.extractUserId(claims);
                String username = jws.getBody().getSubject();
                @SuppressWarnings("unchecked")
                List<String> roles = tokenProvider.extractRoles(claims); // List<String>) jws.getBody().get("roles");

                var principal = new CustomUserDetails(userId, username, "", true, roles);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, jws, principal.getAuthorities());

//                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                        username, null, new CustomUserDetails(userId, username, "", true, roles).getAuthorities());
//                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // 토큰 오류 시 인증정보 미설정 (EntryPoint에서 처리)
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
