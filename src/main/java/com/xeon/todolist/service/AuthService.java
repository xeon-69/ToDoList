package com.xeon.todolist.service;

import com.xeon.todolist.dto.LoginRequest;
import com.xeon.todolist.dto.LoginResponse;
import com.xeon.todolist.dto.RegisterRequest;
import com.xeon.todolist.dto.RegisterResponse;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.repository.UserRepository;
import com.xeon.todolist.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    public RegisterResponse mapToRegisterResponse(Users users) {
        return new RegisterResponse(users.getUsername());
    }

    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        Users user = new Users();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);
        return mapToRegisterResponse(user);
    }


    public LoginResponse verifyUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        String token = jwtService.generateToken(loginRequest.getUsername());
        return new LoginResponse(token);
    }
}
