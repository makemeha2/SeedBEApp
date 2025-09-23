package com.heyso.SeedBEApp.support;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;

public abstract class BaseDbIT {

    private static boolean useTestcontainers;
    private static MariaDBContainer<?> mariadb;

    static {
        try {
            mariadb = new MariaDBContainer<>("mariadb:11.4.2")
                    .withDatabaseName("SeedBEAppDB")
                    .withUsername("root")
                    .withPassword("1234");
            mariadb.start();              // ← 여기서 Docker 없으면 예외
            useTestcontainers = true;
        } catch (Throwable t) {
            useTestcontainers = false;   // ← 자동 폴백: 로컬 DB 사용
        }
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        if (useTestcontainers) {
            r.add("spring.datasource.url", mariadb::getJdbcUrl);
            r.add("spring.datasource.username", mariadb::getUsername);
            r.add("spring.datasource.password", mariadb::getPassword);
            r.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
        } else {
            // 🔶 로컬 테스트 DB로 폴백 (반드시 테스트 전용 DB만 연결!)
            r.add("spring.datasource.url", () -> "jdbc:mariadb://localhost:3306/seedbeapp_test_db");
            r.add("spring.datasource.username", () -> "testuser");
            r.add("spring.datasource.password", () -> "testpass");
            r.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
        }
    }

    @Autowired private Flyway flyway;

    @BeforeEach
    void cleanAndMigrate() {
        // 매 테스트마다 DB 초기화 (테스트 DB에만 적용!)
        flyway.clean();
        flyway.migrate();
    }
}
