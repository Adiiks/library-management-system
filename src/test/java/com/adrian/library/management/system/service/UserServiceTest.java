package com.adrian.library.management.system.service;

import com.adrian.library.management.system.auth.AuthenticationFacade;
import com.adrian.library.management.system.converter.UserConverter;
import com.adrian.library.management.system.dto.UserRequest;
import com.adrian.library.management.system.entity.Gender;
import com.adrian.library.management.system.entity.Role;
import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    UserConverter userConverter = new UserConverter(new BCryptPasswordEncoder());

    @Mock
    AuthenticationFacade authFacade;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userConverter, authFacade);
    }

    @DisplayName("Create new member - Username is already use by another user")
    @Test
    void createNewMemberUsernameIsAlreadyUse() {
        UserRequest userRequest = getUserRequest();

        when(userRepository.existsByUsername(anyString()))
                .thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                userService.createNewMember(userRequest));

        verify(userRepository, times(1))
                .existsByUsername(anyString());

        verify(userRepository, times(0))
                .save(any());
    }

    @DisplayName("Create new member - Success")
    @Test
    void createNewMember() {
        UserRequest userRequest = getUserRequest();

        ArgumentCaptor<User> acUser = ArgumentCaptor.forClass(User.class);

        when(userRepository.existsByUsername(anyString()))
                .thenReturn(false);

        userService.createNewMember(userRequest);

        verify(userRepository, times(1))
                .existsByUsername(anyString());

        verify(userRepository, times(1))
                .save(acUser.capture());

        User userToSave = acUser.getValue();

        assertEquals(Role.ROLE_MEMBER, userToSave.getRole());
        assertNotEquals(userToSave.getPassword(), userRequest.password());
    }

    private UserRequest getUserRequest() {
        return new UserRequest("some address ...", "email@wp.pl", Gender.FEMALE,
                "name", "password", "456444567", "username");
    }

    @DisplayName("Remove member - Success")
    @Test
    void removeMember() {
        userService.removeMember(1);

        verify(userRepository, times(1))
                .deleteById(anyInt());
    }

    @DisplayName("Find user - User not found")
    @Test
    void getUserUserNotFound() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                userService.getUser("username"));
    }

    @DisplayName("Find user")
    @Test
    void getUser() {
        User userFromDb = new User();

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(userFromDb));

        User foundUser = userService.getUser("username");

        assertNotNull(foundUser);
    }

    @DisplayName("Get currently authenticated user")
    @Test
    void getAuthenticatedUser() {
        User userDb = new User();

        when(authFacade.getUsername())
                .thenReturn("username");

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(userDb));

        User user = userService.getAuthenticatedUser();

        assertNotNull(user);
    }
}