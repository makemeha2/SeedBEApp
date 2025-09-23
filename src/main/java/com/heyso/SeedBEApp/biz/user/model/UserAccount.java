package com.heyso.SeedBEApp.biz.user.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount {
    private Long userId;
    private String username;
    private String passwordHash;
    private String name;
    private String email;
    private String useYn; // 'Y' or 'N'
    private List<String> roles; // ROLE_USER, ROLE_ADMIN ...
}
