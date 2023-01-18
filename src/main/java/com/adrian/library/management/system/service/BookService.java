package com.adrian.library.management.system.service;

import com.adrian.library.management.system.converter.BookConverter;
import com.adrian.library.management.system.dto.BookLocation;
import com.adrian.library.management.system.dto.BookRequest;
import com.adrian.library.management.system.dto.BookResponse;
import com.adrian.library.management.system.dto.RequestedBookDTO;
import com.adrian.library.management.system.entity.*;
import com.adrian.library.management.system.repository.BookRepository;
import com.adrian.library.management.system.repository.CategoryRepository;
import com.adrian.library.management.system.repository.RequestedBookRepository;
import com.adrian.library.management.system.repository.ShelfRepository;
import com.adrian.library.management.system.specification.BookSpecification;
import com.adrian.library.management.system.specification.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final ShelfRepository shelfRepository;

    private final BookConverter bookConverter;

    private final UserService userService;

    private final RequestedBookRepository requestedBookRepository;

    private final CategoryRepository categoryRepository;

    public Page<BookResponse> searchBooks(SearchCriteria searchCriteria, Pageable pageable) {
        BookSpecification bookSpec = new BookSpecification(searchCriteria);

        Page<Book> bookPage = bookRepository.findAll(bookSpec, pageable);

        List<BookResponse> bookResponseList = bookPage.getContent()
                .stream()
                .map(bookConverter::bookConvertToBookResponse)
                .toList();

        return new PageImpl<>(bookResponseList, bookPage.getPageable(), bookPage.getTotalElements());
    }

    public BookLocation findBookLocation(Integer bookId) {
        return shelfRepository.findByBooksId(bookId)
                .map(shelf -> new BookLocation(shelf.getShelfNumber(), shelf.getFloor()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Book with id " + bookId + " not found."));
    }

    @Transactional
    public void requestBook(String username, RequestedBookDTO requestedBookDTO) {
        User user = userService.getUser(username);

        RequestedBook requestedBook = RequestedBook.builder()
                .additionalInformation(requestedBookDTO.additionalInformation())
                .author(requestedBookDTO.author())
                .bookTitle(requestedBookDTO.bookTitle())
                .user(user)
                .build();

        requestedBookRepository.save(requestedBook);
    }

    @Transactional
    public void createBook(BookRequest bookRequest) {
        Category bookCategory = findCategory(bookRequest.categoryId());

        Shelf bookLocation = findShelf(bookRequest.shelfId());

        Book book = Book.builder()
                .available(true)
                .title(bookRequest.title())
                .publisher(bookRequest.publisher())
                .dateOfPublication(bookRequest.dateOfPublication())
                .category(bookCategory)
                .author(bookRequest.author())
                .shelf(bookLocation)
                .build();

        bookRepository.save(book);
    }

    private Category findCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Category with id " + categoryId + " not found."));
    }

    private Shelf findShelf(Integer shelfId) {
        return shelfRepository.findById(shelfId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shelf with id " + shelfId + " not found."));
    }

    @Transactional
    public void updateBookAvailable(boolean available, Book book) {
        book.setAvailable(available);

        bookRepository.save(book);
    }
}
