package com.heyso.SeedBEApp.common.exception;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExceptionLogMapper {
    void insert(ExceptionLog log);
}
