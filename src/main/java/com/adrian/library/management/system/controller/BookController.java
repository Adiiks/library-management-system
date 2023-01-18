package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.auth.AuthenticationFacade;
import com.adrian.library.management.system.dto.BookRequest;
import com.adrian.library.management.system.dto.BookResponse;
import com.adrian.library.management.system.dto.BookLocation;
import com.adrian.library.management.system.dto.RequestedBookDTO;
import com.adrian.library.management.system.service.BookService;
import com.adrian.library.management.system.specification.SearchCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthenticationFacade authFacade;

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<BookResponse> searchBooks(@RequestBody(required = false) SearchCriteria searchCriteria,
                                          Pageable pageable) {
        return bookService.searchBooks(searchCriteria, pageable);
    }

    @GetMapping("/book-location/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public BookLocation findBookLocation(@PathVariable Integer bookId) {
        return bookService.findBookLocation(bookId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public void requestBook(@Valid @RequestBody RequestedBookDTO requestedBookDTO) {
        String username = authFacade.getUsername();

        bookService.requestBook(username, requestedBookDTO);
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @PostMapping("/add-book")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBook(@Valid @RequestBody BookRequest bookRequest) {
        bookService.createBook(bookRequest);
    }
}
