package com.example.secondhand.mapper;

import com.example.secondhand.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") Long id);
    int insert(User user);
    int updateCreditScore(@Param("id") Long id, @Param("delta") Integer delta);
}

