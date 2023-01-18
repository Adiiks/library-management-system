package com.adrian.library.management.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookRequest(
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Author is required")
        String author,
        @NotBlank(message = "Publisher is required")
        String publisher,
        @NotNull(message = "Date of publication is required")
        LocalDate dateOfPublication,
        @NotNull(message = "Category id is required")
        Integer categoryId,
        @NotNull(message = "Shelf id is required")
        Integer shelfId
) {
}
