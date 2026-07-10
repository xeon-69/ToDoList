package com.xeon.todolist;

import com.xeon.todolist.dto.LoginRequest;
import com.xeon.todolist.dto.LoginResponse;
import com.xeon.todolist.dto.RegisterRequest;
import com.xeon.todolist.dto.RegisterResponse;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.exception.InvalidCredentialsException;
import com.xeon.todolist.exception.UserAlreadyExistException;
import com.xeon.todolist.repository.UserRepository;
import com.xeon.todolist.security.JWTService;
import com.xeon.todolist.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Test
    void shouldRegisterUser() {

        // requested registration
        RegisterRequest registerRequest = new RegisterRequest("username", "password");


        // what db would hold
        Users savedUser = new Users();
        savedUser.setUsername("username");
        savedUser.setPassword("encodedPassword");


        // final response
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUsername("username");


        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.saveAndFlush(any(Users.class))).thenReturn(savedUser);

        RegisterResponse result = authService.registerUser(registerRequest);


        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);

        // verify first for using capture
        verify(userRepository).saveAndFlush(captor.capture());
        verify(passwordEncoder).encode("password");

        // only extract after verifying
        Users actualUser= captor.getValue();

        // assert after getting the data we need
        assertEquals(registerResponse, result);
        assertEquals("username", actualUser.getUsername());
        assertEquals("encodedPassword", actualUser.getPassword());

    }

    @Test
    void shouldThrowException_WhenUsernameAlreadyExists() {
        // user request
        RegisterRequest registerRequest = new RegisterRequest("existingUsername", "password");

//        when(userRepository.findByUsername("existingUsername")).thenReturn(Optional.of(new Users()));

        when(userRepository.existsByUsername(any())).thenReturn(true);
        // the exception that throws after executing the method
        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class, () -> {
            authService.registerUser(registerRequest);
        });

        // compare the exception messages
        assertEquals("User with username existingUsername already exists", exception.getMessage());

        // ensure db is never saved
        verify(userRepository, never()).saveAndFlush(any(Users.class));
    }

    @Test
    void shouldAuthenticateUser_WhenUserIsValid() {
        LoginRequest loginRequest = new LoginRequest("username", "password");

        String fakeToken = "jwt-fakeToken";

        // we mock with generateToken method and username we set (so that it will work correctly with the same username)
        when(jwtService.generateToken("username")).thenReturn(fakeToken);

        // result that will generate with the same username
        LoginResponse loginResponse = authService.verifyUser(loginRequest);

        assertNotNull(loginResponse);
        // compare the tokens
        assertEquals(fakeToken, loginResponse.getToken());

        // ensure that generateToken is executed
        verify(jwtService).generateToken("username");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowException_WhenUserIsNotValid(){
        LoginRequest loginRequest = new LoginRequest ("username", "wrongPassword");

        // force it to throw the exception
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad Credentials"));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.verifyUser(loginRequest);
        });

        assertEquals("Invalid username or password", exception.getMessage());

        verify(jwtService, never()).generateToken(anyString());
    }
}
