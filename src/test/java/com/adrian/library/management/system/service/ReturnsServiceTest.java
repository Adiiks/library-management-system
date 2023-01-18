package com.adrian.library.management.system.service;

import com.adrian.library.management.system.entity.Borrowing;
import com.adrian.library.management.system.entity.Returns;
import com.adrian.library.management.system.repository.BorrowingRepository;
import com.adrian.library.management.system.repository.ReturnsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnsServiceTest {

    ReturnsService returnsService;

    @Mock
    BorrowingRepository borrowingRepository;

    @Mock
    ReturnsRepository returnsRepository;

    @Mock
    BookService bookService;

    @BeforeEach
    void setUp() {
        returnsService = new ReturnsService(borrowingRepository, returnsRepository, bookService);
    }

    @DisplayName("Return book and calculate fine - Borrowing not found")
    @Test
    void returnBookAndCalculateFineBorrowingNotFound() {
        when(borrowingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                returnsService.returnBookAndCalculateFine(1));
    }

    @DisplayName("Return book and calculate fine - Fine is zero")
    @Test
    void returnBookAndCalculateFineIsZero() {
        Borrowing borrowingDb = Borrowing.builder()
                .dueDate(LocalDate.of(2023, 1, 18))
                .build();

        ArgumentCaptor<Returns> acReturns = ArgumentCaptor.forClass(Returns.class);

        when(borrowingRepository.findById(anyInt()))
                .thenReturn(Optional.of(borrowingDb));

        Integer fine = returnsService.returnBookAndCalculateFine(1);

        verify(returnsRepository, times(1))
                .save(acReturns.capture());

        Returns savedReturns = acReturns.getValue();
        assertNotNull(savedReturns);
        assertEquals(borrowingDb, savedReturns.getBorrowing());
        assertNotNull(savedReturns.getDateReturned());
        assertEquals(fine, savedReturns.getFine());

        verify(bookService, times(1))
                .updateBookAvailable(anyBoolean(), any());

        assertEquals(0, fine);
    }

    @DisplayName("Return book and calculate fine - Fine is greater then zero")
    @Test
    void returnBookAndCalculateFineIsGreaterThenZero() {
        Borrowing borrowingDb = Borrowing.builder()
                .dueDate(LocalDate.of(2023, 1, 1))
                .build();

        ArgumentCaptor<Returns> acReturns = ArgumentCaptor.forClass(Returns.class);

        when(borrowingRepository.findById(anyInt()))
                .thenReturn(Optional.of(borrowingDb));

        Integer fine = returnsService.returnBookAndCalculateFine(1);

        verify(returnsRepository, times(1))
                .save(acReturns.capture());

        Returns savedReturns = acReturns.getValue();
        assertNotNull(savedReturns);
        assertEquals(borrowingDb, savedReturns.getBorrowing());
        assertNotNull(savedReturns.getDateReturned());
        assertEquals(fine, savedReturns.getFine());

        verify(bookService, times(1))
                .updateBookAvailable(anyBoolean(), any());

        assertThat(fine, greaterThan(0));
    }
}