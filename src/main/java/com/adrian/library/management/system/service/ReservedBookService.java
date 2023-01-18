package com.adrian.library.management.system.service;

import com.adrian.library.management.system.entity.Book;
import com.adrian.library.management.system.entity.ReservedBook;
import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.repository.BookRepository;
import com.adrian.library.management.system.repository.ReservedBookRepository;
import com.adrian.library.management.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReservedBookService {

    private final ReservedBookRepository reservedBookRepository;

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    @Transactional
    public void reserveBook(String username, Integer bookId) {
        Book book = findBook(bookId);

        if (!book.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Book is not available.");
        }

        User user = findUser(username);

        book.setAvailable(false);

        ReservedBook reservedBook = ReservedBook.builder()
                .book(book)
                .user(user)
                .build();

        reservedBookRepository.save(reservedBook);
    }

    private Book findBook(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Book with id " + bookId + " not found."));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User with username " + username + " not found."));
    }
}
