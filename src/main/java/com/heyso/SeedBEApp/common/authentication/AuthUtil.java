package com.heyso.SeedBEApp.common.authentication;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    private AuthUtil(){}

    public static Long currentUserIdOrNull() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || a instanceof AnonymousAuthenticationToken) return null;
        Object p = a.getPrincipal();
        if (p instanceof CustomUserDetails cud) return cud.getUserId();
        return null;
    }

    public static Long requireUserId() {
        Long uid = currentUserIdOrNull();
        if (uid == null) throw new org.springframework.security.access.AccessDeniedException("로그인이 필요합니다.");
        return uid;
    }

    public static boolean hasRole(String role) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return false;
        String target = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        for (GrantedAuthority ga : a.getAuthorities()) {
            if (target.equals(ga.getAuthority())) return true;
        }
        return false;
    }
}
