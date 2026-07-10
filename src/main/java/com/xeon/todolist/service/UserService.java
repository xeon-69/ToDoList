package com.xeon.todolist.service;

import com.xeon.todolist.dto.UpdateUserRequest;
import com.xeon.todolist.dto.UserResponse;
import com.xeon.todolist.exception.UserNotFoundException;
import com.xeon.todolist.mapper.UserMapper;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public Users findUserById(long id){
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public UserResponse updateUser(long userId, UpdateUserRequest updateUserRequest){
        Users user = findUserById(userId);
        user.setUsername(updateUserRequest.getName());
        user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        Users updatedUser = userRepository.saveAndFlush(user);
        log.info("Updated successfully for userID '{}'", userId);
        return userMapper.toResponse(updatedUser);
    }

    public List<UserResponse> getAllUser(){
        log.debug("Getting all users...");
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }


    @Transactional
    public void deleteUser(long userId){
        Users user = findUserById(userId);
        userRepository.delete(user);
        log.info("User with ID '{}' deleted successfully", userId);
    }
}
