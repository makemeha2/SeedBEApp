package com.heyso.SeedBEApp.support;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDbIT {

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void cleanAndMigrate() {
        flyway.clean();   // ⚠️ 전용 Test DB일 때만 안전!
        flyway.migrate();
    }
}
