package com.xeon.todolist.service;

import com.xeon.todolist.dto.UpdateUserRequest;
import com.xeon.todolist.dto.UserResponse;
import com.xeon.todolist.exception.UserNotFoundException;
import com.xeon.todolist.mapper.UserMapper;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse updateUser(long userId, UpdateUserRequest updateUserRequest){
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setUsername(updateUserRequest.getName());
        user.setPassword(updateUserRequest.getPassword());
        return UserMapper.toResponse(user);
    }

    public List<UserResponse> getAllUser(){
        return userRepository.findAll().stream().map(UserMapper::toResponse).toList();
    }

    public void deleteUser(long userId){
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }
}
