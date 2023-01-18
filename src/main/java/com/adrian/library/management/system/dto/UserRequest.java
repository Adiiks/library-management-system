package com.adrian.library.management.system.dto;

import com.adrian.library.management.system.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserRequest(
        @NotBlank(message = "Address is required")
        String address,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,
        @NotNull(message = "Gender is required")
        Gender gender,
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Password is required")
        String password,
        @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number")
        String phone,
        @NotBlank(message = "Username is required")
        String username
                          ) {
}
