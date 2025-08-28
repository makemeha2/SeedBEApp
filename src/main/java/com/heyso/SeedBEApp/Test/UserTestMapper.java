package com.heyso.SeedBEApp.Test;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserTestMapper {
    @Select("SELECT * FROM user_test")
    List<UserTestDto> findAll();

    @Insert("INSERT INTO user_test(username, email) VALUES(#{username}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserTestDto user);
}
