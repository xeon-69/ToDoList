package com.xeon.todolist;

import com.xeon.todolist.dto.UpdateUserRequest;
import com.xeon.todolist.dto.UserResponse;
import com.xeon.todolist.entity.Users;
import com.xeon.todolist.exception.UserNotFoundException;
import com.xeon.todolist.mapper.UserMapper;
import com.xeon.todolist.repository.UserRepository;
import com.xeon.todolist.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository; // fake the db layer with mockito

    @InjectMocks
    private UserService userService; // injects the fake db layer into service layer

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Test
    void shouldReturnTheUser_WhenFindById() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Users foundUser = userService.findUserById(1L);
        assertEquals(1L, foundUser.getId());
        assertEquals("username", foundUser.getUsername());
        assertEquals("password", foundUser.getPassword());
        verify(userRepository).findById(1L); // "Confirm that userRepository.findById(1L) was called exactly once."
    }

    @Test
    void shouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // Add assertion for the expected exception
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));
        verify(userRepository, times(1)).findById(1L); // call one time

    }

    @Test
    void shouldUpdateUser_WhenUserExists() {
        Users existingUser = new Users();
        existingUser.setId(1L);
        existingUser.setUsername("oldUsername");
        existingUser.setPassword("oldPassword");

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("newUsername");
        updateUserRequest.setPassword("newPassword");

        UserResponse mappedResponse = new UserResponse();
        mappedResponse.setName("newUsername");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.saveAndFlush(existingUser)).thenReturn(existingUser);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userMapper.toResponse(existingUser)).thenReturn(mappedResponse);


        UserResponse result = userService.updateUser(1L, updateUserRequest);

        assertEquals("newUsername", existingUser.getUsername());
        assertEquals("encodedNewPassword", existingUser.getPassword());
        assertEquals("newUsername", result.getName());

        verify(userRepository).findById(1L);
        verify(userRepository).saveAndFlush(existingUser);
        verify(passwordEncoder).encode("newPassword");
        verify(userMapper).toResponse(existingUser);

    }

    @Test
    void shouldDeleteUser_WhenUserExists(){
        Users existingUser = new Users();
        existingUser.setId(1L);
        existingUser.setUsername("Username");
        existingUser.setPassword("Password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).delete(existingUser);
    }

    @Test
    void shouldThrowException_WhenDeletingUserDoesNotExist(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any(Users.class));
    }

}
