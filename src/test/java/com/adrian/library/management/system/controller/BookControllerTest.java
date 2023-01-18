package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.auth.AuthenticationFacade;
import com.adrian.library.management.system.dto.BookLocation;
import com.adrian.library.management.system.dto.BookRequest;
import com.adrian.library.management.system.dto.BookResponse;
import com.adrian.library.management.system.dto.RequestedBookDTO;
import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.service.BookService;
import com.adrian.library.management.system.specification.SearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    final String BASE_URL = "/api/books";

    MockMvc mockMvc;

    BookController bookController;

    @Mock
    BookService bookService;

    @Mock
    AuthenticationFacade authFacade;

    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        bookController = new BookController(bookService, authFacade);

        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new ValidationExceptionHandler())
                .build();
    }

    @DisplayName("Search books")
    @Test
    void searchBooks() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria("title", "Harry Potter");

        Page<BookResponse> bookResponsePage = new PageImpl<>(
                List.of(getBookResponse()), Pageable.unpaged(), 1
        );

        when(bookService.searchBooks(any(), any()))
                .thenReturn(bookResponsePage);

        mockMvc.perform(post(BASE_URL + "/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(searchCriteria))
                    .param("size", "20")
                    .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(bookResponsePage)));
    }

    private BookResponse getBookResponse() {
        return new BookResponse(1, "title", "author", "publisher",
                LocalDate.now(), "categoryName");
    }

    @DisplayName("Find book location")
    @Test
    void findBookLocation() throws Exception {
        BookLocation bookLocation = new BookLocation(1, 0);

        when(bookService.findBookLocation(anyInt()))
                .thenReturn(bookLocation);

        mockMvc.perform(get(BASE_URL + "/book-location/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(bookLocation)));
    }

    @DisplayName("Request a book - Validation failed")
    @Test
    void requestBookValidationFailed() throws Exception {
        RequestedBookDTO requestedBookDTO = new RequestedBookDTO("", "", null);

        mockMvc.perform(post(BASE_URL + "/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestedBookDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @DisplayName("Request a book")
    @Test
    void requestBook() throws Exception {
        RequestedBookDTO requestedBookDTO = new RequestedBookDTO("title", "author",
                null);

        when(authFacade.getUsername())
                .thenReturn("username");

        mockMvc.perform(post(BASE_URL + "/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestedBookDTO)))
                .andExpect(status().isCreated());
    }

    @DisplayName("Add new book - Validation failed")
    @Test
    void addBookValidationFailed() throws Exception {
        BookRequest bookRequest = new BookRequest("", "", "", null,
                null, null);

        mockMvc.perform(post(BASE_URL + "/add-book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(6)));
    }

    @DisplayName("Add new book")
    @Test
    void addBook() throws Exception {
        BookRequest bookRequest = getValidBookRequest();

        mockMvc.perform(post(BASE_URL + "/add-book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated());
    }

    private BookRequest getValidBookRequest() {
        return new BookRequest("title", "author", "publisher",
                LocalDate.now(), 1, 1);
    }
}