package com.heyso.SeedBEApp.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SeedBEApp API",
                version = "v1",
                description = "SeedBEApp의 공용 백엔드 API 명세",
                contact = @Contact(name = "heyso", email = "you@example.com")
        ),
        servers = {
                @Server(url = "http://localhost:19090", description = "Local"),
                @Server(url = "http://127.0.0.1:19090", description = "Local (127.0.0.1)")
        }
)
// (선택) JWT 등을 쓴다면 SecurityScheme 예시
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SpringDocConfig {

}
