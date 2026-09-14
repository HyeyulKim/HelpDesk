package com.helpdesk.user.mapper;

import com.helpdesk.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User findByUsername(@Param("username") String username);
    int countByUsername(@Param("username") String username);
    int countByEmail(@Param("email") String email);
}
