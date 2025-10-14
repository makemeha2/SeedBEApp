package com.heyso.SeedBEApp.common.authentication;

import com.heyso.SeedBEApp.biz.user.model.UserAccount;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessExpMs;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.access-exp-min}") long accessExpMin,
            @Value("${app.security.jwt.issuer}") String issuer
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMs = accessExpMin * 60_000L;
        this.issuer = issuer;
    }

    public String createAccessToken(UserAccount user, List<String> roles) {
        long now = System.currentTimeMillis();
        Claims claims = Jwts.claims().setSubject(user.getUserId().toString());
        claims.put("username", user.getUsername());
        claims.put("name", user.getName());
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessExpMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Long extractUserId(Claims claims) {
        Object v = claims.get("sub");
        return (v == null) ? null : Long.valueOf(String.valueOf(v));
    }

    public String extractUsername(Claims claims) {
        Object v = claims.get("username");
        return (v == null) ? null : String.valueOf(v);
    }

    public String extractName(Claims claims) {
        Object v = claims.get("name");
        return (v == null) ? null : String.valueOf(v);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Claims claims) {
        Object v = claims.get("roles");
        if (v instanceof List<?> list) {
            return (List<String>) list;
        }
        else
        {
            return Collections.emptyList();
        }
    }
}
