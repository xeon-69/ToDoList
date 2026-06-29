package com.xeon.todolist.mapper;

import com.xeon.todolist.dto.UpdateUserRequest;
import com.xeon.todolist.dto.UserResponse;
import com.xeon.todolist.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(Users user){
        return UserResponse.builder()
                .userId(user.getId())
                .name(user.getUsername())
                .build();
    }

    public Users toEntity(UpdateUserRequest updateUserRequest){
        return Users.builder()
                .username(updateUserRequest.getName())
                .password(updateUserRequest.getPassword())
                .build();
    }
}
