package com.adrian.library.management.system.service;

import com.adrian.library.management.system.entity.Borrowing;
import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    public static final int RENEWAL_DAYS = 7;

    private final BorrowingRepository borrowingRepository;

    @Transactional
    public void renewBook(User authenticatedUser, Integer borrowingId) {
        Borrowing borrowingDb = borrowingRepository.findByIdAndUser(borrowingId, authenticatedUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Borrowing with id " + borrowingId + " and userId "
                                + authenticatedUser.getId() + " not found."));

        LocalDate newDueDate = borrowingDb.getDueDate().plusDays(RENEWAL_DAYS);
        borrowingDb.setDueDate(newDueDate);

        borrowingRepository.save(borrowingDb);
    }
}
