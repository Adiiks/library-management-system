package com.adrian.library.management.system.service;

import com.adrian.library.management.system.auth.AuthenticationFacade;
import com.adrian.library.management.system.converter.UserConverter;
import com.adrian.library.management.system.dto.UserRequest;
import com.adrian.library.management.system.entity.Role;
import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final AuthenticationFacade authFacade;

    @Transactional
    public void createNewMember(UserRequest userRequest) {
        // check if username is already use by another user
        if (userRepository.existsByUsername(userRequest.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username " + userRequest.username() + " already in use by another user");
        }

        // create and save new user
        User user = userConverter.userRequestConvertToUser(userRequest);

        user.setRole(Role.ROLE_MEMBER);

        userRepository.save(user);
    }

    @Transactional
    public void removeMember(Integer userId) {
        userRepository.deleteById(userId);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User with username " + username + " not found"));
    }

    public User getAuthenticatedUser() {
        String username = authFacade.getUsername();

        return getUser(username);
    }
}
