package com.adrian.library.management.system.controller;

import com.adrian.library.management.system.dto.UserRequest;
import com.adrian.library.management.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @PostMapping("/add-member")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewMember(@Valid @RequestBody UserRequest userRequest) {
        userService.createNewMember(userRequest);
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @DeleteMapping("/remove-member/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Integer userId) {
        userService.removeMember(userId);
    }
}
