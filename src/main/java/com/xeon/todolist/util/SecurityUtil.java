package com.xeon.todolist.util;

import com.xeon.todolist.entity.Users;
import com.xeon.todolist.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Users getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        UserPrincipal userPrincipal1 = (UserPrincipal) authentication.getPrincipal();
        assert userPrincipal1 != null;
        return userPrincipal1.getUser();
    }
}
