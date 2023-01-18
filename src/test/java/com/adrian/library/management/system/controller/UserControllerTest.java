package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.dto.UserRequest;
import com.adrian.library.management.system.entity.Gender;
import com.adrian.library.management.system.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    MockMvc mockMvc;

    UserController userController;

    @Mock
    UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ValidationExceptionHandler())
                .build();
    }

    @DisplayName("Add new member - Validation failed")
    @Test
    void createNewMemberValidationFailed() throws Exception {
        UserRequest userRequest = new UserRequest("", "email", null, "",
                "", "45644", "");

        mockMvc.perform(post("/api/users/add-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(7)));

        verify(userService, times(0)).createNewMember(any());
    }

    @DisplayName("Add new member - Success")
    @Test
    void createNewMember() throws Exception {
        UserRequest userRequest = getValidUserRequest();

        mockMvc.perform(post("/api/users/add-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createNewMember(any());
    }

    private UserRequest getValidUserRequest() {
        return new UserRequest("some address ...", "email@wp.pl", Gender.FEMALE, "name",
                "password", "456444567", "username");
    }

    @DisplayName("Remove member - Success")
    @Test
    void removeMember() throws Exception {
        mockMvc.perform(delete("/api/users/remove-member/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1))
                .removeMember(anyInt());
    }
}