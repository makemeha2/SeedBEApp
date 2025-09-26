package com.heyso.SeedBEApp.support;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestFlywayConfig {
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        // 컨텍스트 시작 시점에 clean -> migrate 수행
        return flyway -> {
            flyway.clean();    // ⚠ 테스트 전용 DB에서만 사용
            flyway.migrate();
        };
    }
}
