package com.heyso.SeedBEApp.biz.board.security;

import com.heyso.SeedBEApp.common.authentication.EndpointSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class BoardEndpointSecurity implements EndpointSecurity {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        NoticeConfigure(auth);
    }

    private void NoticeConfigure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/api/notice/**").permitAll();
        // auth.requestMatchers(HttpMethod.POST,
        //     "/api/notice/replylist",
        //     "/api/notice/files"
        // ).permitAll();
        auth.requestMatchers(HttpMethod.POST,   "/api/notice/**").authenticated();
        auth.requestMatchers(HttpMethod.PUT,    "/api/notice/**").authenticated();
        auth.requestMatchers(HttpMethod.PATCH,  "/api/notice/**").authenticated();
        auth.requestMatchers(HttpMethod.DELETE, "/api/notice/**").authenticated();
    }
}
