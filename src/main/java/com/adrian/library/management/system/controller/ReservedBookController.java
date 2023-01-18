package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.auth.AuthenticationFacade;
import com.adrian.library.management.system.service.ReservedBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reserved-books")
@RequiredArgsConstructor
public class ReservedBookController {

    private final ReservedBookService reservedBookService;

    private final AuthenticationFacade authFacade;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reserve/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reserveBook(@PathVariable Integer bookId) {
        String username = authFacade.getUsername();

        reservedBookService.reserveBook(username, bookId);
    }
}
