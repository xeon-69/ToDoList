package com.xeon.todolist.controller;

import com.xeon.todolist.dto.*;
import com.xeon.todolist.service.AuthService;
import com.xeon.todolist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @DeleteMapping("{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @PutMapping("{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable long userId, @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse userResponse = userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(userResponse);
    }

}
