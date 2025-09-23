package com.heyso.SeedBEApp.biz.user.dao;

import com.heyso.SeedBEApp.biz.user.model.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    UserAccount findByUsername(@Param("username") String username);
    List<String> findRoles(@Param("userId") Long userId);
}
