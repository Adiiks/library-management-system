package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.service.ReturnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnsController {

    private final ReturnsService returnsService;

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @PostMapping("/return-book/{borrowingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Integer returnBook(@PathVariable Integer borrowingId) {
        Integer fine = returnsService.returnBookAndCalculateFine(borrowingId);

        return fine;
    }
}
