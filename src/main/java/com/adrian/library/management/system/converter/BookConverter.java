package com.adrian.library.management.system.converter;

import com.adrian.library.management.system.dto.BookResponse;
import com.adrian.library.management.system.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookConverter {

    public BookResponse bookConvertToBookResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getPublisher(),
                book.getDateOfPublication(), book.getCategory().getName());
    }
}
