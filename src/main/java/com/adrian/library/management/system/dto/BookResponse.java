package com.adrian.library.management.system.dto;

import java.time.LocalDate;

public record BookResponse(
        Integer id,
        String title,
        String author,
        String publisher,
        LocalDate dateOfPublication,
        String categoryName
) {
}
