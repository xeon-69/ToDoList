package com.xeon.todolist.controller;

import com.xeon.todolist.dto.LoginRequest;
import com.xeon.todolist.dto.LoginResponse;
import com.xeon.todolist.dto.RegisterRequest;
import com.xeon.todolist.dto.RegisterResponse;
import com.xeon.todolist.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest){
        return authService.registerUser(registerRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        return authService.verifyUser(loginRequest);
    }

}
