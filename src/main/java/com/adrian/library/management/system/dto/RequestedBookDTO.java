package com.adrian.library.management.system.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestedBookDTO(
        @NotBlank(message = "Title is required")
        String bookTitle,
        @NotBlank(message = "Author is required")
        String author,
        String additionalInformation
) {
}
