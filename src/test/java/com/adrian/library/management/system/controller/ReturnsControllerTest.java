package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.service.ReturnsService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReturnsControllerTest {

    final String BASE_URL = "/api/returns";

    MockMvc mockMvc;

    ReturnsController returnsController;

    @Mock
    ReturnsService returnsService;

    @BeforeEach
    void setUp() {
        returnsController = new ReturnsController(returnsService);

        mockMvc = MockMvcBuilders.standaloneSetup(returnsController).build();
    }

    @DisplayName("Return a book and calculate fine")
    @Test
    void returnBook() throws Exception {
        when(returnsService.returnBookAndCalculateFine(anyInt()))
                .thenReturn(50);

        mockMvc.perform(post(BASE_URL + "/return-book/1"))
                .andExpect(status().isCreated())
                .andExpect(content().string("50"));
    }
}