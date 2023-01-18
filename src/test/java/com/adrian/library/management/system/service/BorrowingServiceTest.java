package com.adrian.library.management.system.service;

import com.adrian.library.management.system.entity.Borrowing;
import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.repository.BorrowingRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    BorrowingService borrowingService;

    @Mock
    BorrowingRepository borrowingRepository;

    @BeforeEach
    void setUp() {
        borrowingService = new BorrowingService(borrowingRepository);
    }

    @DisplayName("Renew a book - Borrowing not found")
    @Test
    void renewBookBorrowingNotFound() {
        User authenticatedUser = new User();

        when(borrowingRepository.findByIdAndUser(anyInt(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                borrowingService.renewBook(authenticatedUser, 1));
    }

    @DisplayName("Renew a book")
    @Test
    void renewBookBorrowing() {
        User authenticatedUser = new User();

        ArgumentCaptor<Borrowing> acBorrowing = ArgumentCaptor.forClass(Borrowing.class);

        LocalDate dueDate = LocalDate.of(2023, 1, 16);
        Borrowing borrowingDb = Borrowing.builder()
                .dueDate(dueDate)
                .build();

        when(borrowingRepository.findByIdAndUser(anyInt(), any()))
                .thenReturn(Optional.of(borrowingDb));

        borrowingService.renewBook(authenticatedUser, 1);

        verify(borrowingRepository, times(1))
                .save(acBorrowing.capture());

        Borrowing borrowing = acBorrowing.getValue();

        assertEquals(dueDate.plusDays(BorrowingService.RENEWAL_DAYS),
                borrowing.getDueDate());
    }
}