package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.auth.AuthenticationFacade;
import com.adrian.library.management.system.service.ReservedBookService;
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
class ReservedBookControllerTest {

    private final String BASE_URL = "/api/reserved-books";

    MockMvc mockMvc;

    ReservedBookController reservedBookController;

    @Mock
    ReservedBookService reservedBookService;

    @Mock
    AuthenticationFacade authFacade;

    @BeforeEach
    void setUp() {
        reservedBookController = new ReservedBookController(reservedBookService, authFacade);

        mockMvc = MockMvcBuilders.standaloneSetup(reservedBookController).build();
    }

    @DisplayName("Reserve a book")
    @Test
    void reserveBook() throws Exception {
        when(authFacade.getUsername())
                .thenReturn("username");

        mockMvc.perform(post(BASE_URL + "/reserve/1"))
                .andExpect(status().isCreated());

        verify(reservedBookService, times(1))
                .reserveBook(anyString(), anyInt());
    }
}