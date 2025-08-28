package com.heyso.SeedBEApp.Test;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTestDto {
    private Integer id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
