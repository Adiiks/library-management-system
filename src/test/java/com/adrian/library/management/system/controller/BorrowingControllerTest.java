package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.service.BorrowingService;
import com.adrian.library.management.system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BorrowingControllerTest {

    final String BASE_URL = "/api/borrowings";

    MockMvc mockMvc;

    BorrowingController borrowingController;

    @Mock
    BorrowingService borrowingService;

    @Mock
    UserService userService;

    User authenticatedUser;

    @BeforeEach
    void setUp() {
        borrowingController = new BorrowingController(borrowingService, userService);

        mockMvc = MockMvcBuilders.standaloneSetup(borrowingController).build();

        authenticatedUser = new User();
    }

    @DisplayName("Renew a book")
    @Test
    void renewBook() throws Exception {
        when(userService.getAuthenticatedUser())
                .thenReturn(authenticatedUser);

        mockMvc.perform(patch(BASE_URL + "/renew-book/1"))
                .andExpect(status().isNoContent());

        verify(borrowingService, times(1))
                .renewBook(any(), anyInt());
    }
}