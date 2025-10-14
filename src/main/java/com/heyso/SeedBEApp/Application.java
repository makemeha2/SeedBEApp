package com.heyso.SeedBEApp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

        System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password"));
    }


}
