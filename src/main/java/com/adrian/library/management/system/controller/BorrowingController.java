package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.entity.User;
import com.adrian.library.management.system.service.BorrowingService;
import com.adrian.library.management.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/renew-book/{borrowingId}")
    public void renewBook(@PathVariable Integer borrowingId) {
        User authenticatedUser = userService.getAuthenticatedUser();

        borrowingService.renewBook(authenticatedUser, borrowingId);
    }
}
