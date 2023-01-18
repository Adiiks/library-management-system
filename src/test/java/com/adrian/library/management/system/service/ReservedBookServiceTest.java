package com.adrian.library.management.system.service;

import com.adrian.library.management.system.entity.Book;
import com.adrian.library.management.system.entity.ReservedBook;
import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.repository.BookRepository;
import com.adrian.library.management.system.repository.ReservedBookRepository;
import com.adrian.library.management.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservedBookServiceTest {

    ReservedBookService reservedBookService;

    @Mock
    ReservedBookRepository reservedBookRepository;

    @Mock
    BookRepository bookRepository;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        reservedBookService = new ReservedBookService(reservedBookRepository, bookRepository,
                userRepository);
    }

    @DisplayName("Reserve a book - Book not found")
    @Test
    void reserveBookNotFound() {
        when(bookRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                reservedBookService.reserveBook("username", 1));
    }

    @DisplayName("Reserve a book - Book is not available")
    @Test
    void reserveBookNotAvailable() {
        Book book = Book.builder()
                .available(false)
                .build();

        when(bookRepository.findById(anyInt()))
                .thenReturn(Optional.of(book));

        assertThrows(ResponseStatusException.class, () ->
                reservedBookService.reserveBook("username", 1));
    }

    @DisplayName("Reserve a book - User not found")
    @Test
    void reserveBookUserNotFound() {
        Book book = Book.builder()
                .available(true)
                .build();

        when(bookRepository.findById(anyInt()))
                .thenReturn(Optional.of(book));

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                reservedBookService.reserveBook("username", 1));
    }

    @DisplayName("Reserve a book")
    @Test
    void reserveBook() {
        Book book = Book.builder()
                .available(true)
                .build();

        User user = new User();

        ArgumentCaptor<ReservedBook> acReservedBook = ArgumentCaptor.forClass(ReservedBook.class);

        when(bookRepository.findById(anyInt()))
                .thenReturn(Optional.of(book));

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        reservedBookService.reserveBook("username", 1);

        verify(reservedBookRepository, times(1))
                .save(acReservedBook.capture());

        ReservedBook savedReservedBook = acReservedBook.getValue();

        assertEquals(book, savedReservedBook.getBook());
        assertEquals(user, savedReservedBook.getUser());
        assertEquals(false, savedReservedBook.getBook().getAvailable());
    }

}