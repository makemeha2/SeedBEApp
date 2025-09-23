package com.heyso.SeedBEApp.support;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// @Testcontainers
public class BaseMariaDbIT {
    @Container
    protected static final MariaDBContainer<?> mariadb =
            new MariaDBContainer<>("mariadb:11.4.2")
                    .withDatabaseName("SeedBEAppDB")
                    .withUsername("root")
                    .withPassword("1234");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mariadb::getJdbcUrl);
        r.add("spring.datasource.username", mariadb::getUsername);
        r.add("spring.datasource.password", mariadb::getPassword);
        r.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
        // 필요시 hikari/maxLifetime, connectionInitSql 등 추가 가능
    }

    @Autowired
    private Flyway flyway; // 스프링 부트가 자동 구성한 Flyway 빈

    @BeforeEach
    void cleanAndMigrate() {
        // 모든 테스트 케이스가 "같은 컨테이너"를 공유하더라도,
        // 매 케이스마다 깨끗한 상태를 보장
        flyway.clean();
        flyway.migrate();
    }
}
