package com.heyso.SeedBEApp.common.authentication;

import com.heyso.SeedBEApp.biz.user.dao.UserMapper;
import com.heyso.SeedBEApp.biz.user.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount u = userMapper.findByUsername(username);
        if (u == null || !"Y".equalsIgnoreCase(u.getUseYn())) {
            throw new UsernameNotFoundException("User not found or disabled: " + username);
        }
        u.setRoles(userMapper.findRoles(u.getUserId()));
        return new CustomUserDetails(u.getUserId(), u.getUsername(), u.getName(), u.getPasswordHash(), true, u.getRoles());
    }
}
