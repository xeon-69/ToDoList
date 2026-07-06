package com.xeon.todolist.service;

import com.xeon.todolist.dto.LoginRequest;
import com.xeon.todolist.dto.LoginResponse;
import com.xeon.todolist.dto.RegisterRequest;
import com.xeon.todolist.dto.RegisterResponse;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.repository.UserRepository;
import com.xeon.todolist.security.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    public RegisterResponse mapToRegisterResponse(Users users) {
        return new RegisterResponse(users.getUsername());
    }

    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        log.debug("Checking if user exists with username {} ...", registerRequest.getUsername());
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("User with username {} already exists", registerRequest.getUsername());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        log.debug("Registering user {} ...", registerRequest.getUsername());
        Users user = new Users();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);
        log.info("User {} registered successfully!", registerRequest.getUsername());
        return mapToRegisterResponse(user);
    }


    public LoginResponse verifyUser(LoginRequest loginRequest) {
        log.debug("Processing login request for the user {}", loginRequest.getUsername());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
            log.debug("Authentication successful for the user: {}. Generating JWT token...", loginRequest.getUsername());
            String token = jwtService.generateToken(loginRequest.getUsername());
            log.info("Logged in successfully for the user {}", loginRequest.getUsername());
            return new LoginResponse(token);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for the user {}. Reason: {}", loginRequest.getUsername(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
