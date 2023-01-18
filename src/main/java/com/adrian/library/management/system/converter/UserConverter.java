package com.adrian.library.management.system.converter;

import com.adrian.library.management.system.dto.UserRequest;
import com.adrian.library.management.system.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    public User userRequestConvertToUser(UserRequest userRequest) {
        return User.builder()
                .address(userRequest.address())
                .email(userRequest.email())
                .gender(userRequest.gender())
                .password(passwordEncoder.encode(userRequest.password()))
                .username(userRequest.username())
                .name(userRequest.name())
                .phone(userRequest.phone())
                .build();
    }
}
