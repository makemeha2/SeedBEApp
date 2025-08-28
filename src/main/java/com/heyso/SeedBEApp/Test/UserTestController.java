package com.heyso.SeedBEApp.Test;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-test")
public class UserTestController {
    private final UserTestMapper userTestMapper;

    @GetMapping
    public List<UserTestDto> getAllUsers() {
        return userTestMapper.findAll();
    }

    @PostMapping
    public UserTestDto addUser(@RequestBody UserTestDto user) {
        userTestMapper.insert(user);
        return user;
    }
}
