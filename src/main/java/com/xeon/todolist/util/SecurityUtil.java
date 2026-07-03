package com.xeon.todolist.util;

import com.xeon.todolist.entity.Users;
import com.xeon.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

//    public Users getCurrentUser(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        assert authentication != null;
//        UserPrincipal userPrincipal1 = (UserPrincipal) authentication.getPrincipal();
//        assert userPrincipal1 != null;
//        return userPrincipal1.getUser();
//    }

    public Users getCurrentUser() {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
