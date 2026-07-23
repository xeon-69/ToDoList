package com.xeon.todolist;

import com.xeon.todolist.controller.AuthController;
import com.xeon.todolist.dto.LoginRequest;
import com.xeon.todolist.dto.LoginResponse;
import com.xeon.todolist.dto.RegisterRequest;
import com.xeon.todolist.dto.RegisterResponse;
import com.xeon.todolist.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnLoginResponse_WhenLoggedIn() throws Exception {

        LoginRequest loginRequest = new LoginRequest("John", "John123");

        LoginResponse loginResponse = new LoginResponse("john-fake-token");

        Mockito.when(authService.verifyUser(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON) // to read the request body and parse it to json
                        .content(objectMapper.writeValueAsString(loginRequest))) // to produce {"username":"John","password":"John123"}
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("john-fake-token"));

        Mockito.verify(authService, Mockito.times(1)).verifyUser(any(LoginRequest.class));
    }

    @Test
    void shouldRegisterTheUser() throws Exception {

        RegisterRequest registerRequest = RegisterRequest.builder().username("User").password("Password").build();

        RegisterResponse registerResponse = RegisterResponse.builder().username("User").build();

        Mockito.when(authService.registerUser(any(RegisterRequest.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("User"));


        Mockito.verify(authService, Mockito.times(1)).registerUser(any(RegisterRequest.class));
    }





}