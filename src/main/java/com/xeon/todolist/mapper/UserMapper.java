package com.xeon.todolist.mapper;

import com.xeon.todolist.dto.UpdateUserRequest;
import com.xeon.todolist.dto.UserResponse;
import com.xeon.todolist.entity.Users;

public class UserMapper {
    public static UserResponse toResponse(Users user){
        return UserResponse.builder()
                .userId(user.getId())
                .name(user.getUsername())
                .build();
    }

    public static Users toEntity(UpdateUserRequest updateUserRequest){
        return Users.builder()
                .username(updateUserRequest.getName())
                .password(updateUserRequest.getPassword())
                .build();
    }
}
