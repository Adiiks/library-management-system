package com.adrian.library.management.system.service;

import com.adrian.library.management.system.entity.Borrowing;
import com.adrian.library.management.system.entity.Returns;
import com.adrian.library.management.system.repository.BorrowingRepository;
import com.adrian.library.management.system.repository.ReturnsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class ReturnsService {

    public static final Integer FINE_PER_DAY = 2;

    private final BorrowingRepository borrowingRepository;
    private final ReturnsRepository returnsRepository;
    private final BookService bookService;

    @Transactional
    public Integer returnBookAndCalculateFine(Integer borrowingId) {
        Borrowing borrowingDb = findBorrowing(borrowingId);

        Integer fine = calculateFine(borrowingDb.getDueDate());

        Returns returns = Returns.builder()
                .borrowing(borrowingDb)
                .dateReturned(LocalDate.now())
                .fine(fine)
                .build();

        returnsRepository.save(returns);

        bookService.updateBookAvailable(true, borrowingDb.getBook());

        return fine;
    }

    private Integer calculateFine(LocalDate dueDate) {
        LocalDate currentDate = LocalDate.now();

        if (currentDate.isAfter(dueDate)) {
            long daysAfterDueDate = DAYS.between(dueDate, currentDate);

            return (int) daysAfterDueDate * FINE_PER_DAY;
        }
        else {
            return 0;
        }
    }

    private Borrowing findBorrowing(Integer borrowingId) {
        return borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Borrowing with id " + borrowingId + " not found."));
    }
}
