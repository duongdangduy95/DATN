package com.example.iotbackend.security;

import com.example.iotbackend.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();

        if (principal instanceof WebUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new RuntimeException("Unauthenticated");
    }
}
