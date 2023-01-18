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
import com.adrian.library.management.system.specification.SearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    BookService bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    ShelfRepository shelfRepository;

    BookConverter bookConverter = new BookConverter();

    @Mock
    UserService userService;

    @Mock
    RequestedBookRepository requestedBookRepository;

    @Mock
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, shelfRepository, bookConverter, userService,
                requestedBookRepository, categoryRepository);
    }

    @DisplayName("Search books")
    @Test
    void searchBooks() {
        SearchCriteria searchCriteria = new SearchCriteria("title", "title");

        Page<Book> bookPage = new PageImpl<>(
                List.of(getBook()), Pageable.unpaged(), 1
        );

        when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(bookPage);

        Page<BookResponse> bookResponsePage = bookService.searchBooks(searchCriteria, Pageable.unpaged());

        assertNotNull(bookResponsePage);
        assertEquals(1, bookResponsePage.getContent().size());
    }

    private Book getBook() {
        return Book.builder()
                .id(1)
                .author("author")
                .category(new Category(1, "categoryName", "details", null))
                .dateOfPublication(LocalDate.now())
                .publisher("publisher")
                .title("title")
                .build();
    }

    @DisplayName("Find book location - Book not found")
    @Test
    void findBookLocationBookNotFound() {
        when(shelfRepository.findByBooksId(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                bookService.findBookLocation(1));
    }

    @DisplayName("Find book location")
    @Test
    void findBookLocation() {
        Shelf shelfDb = getShelf();

        when(shelfRepository.findByBooksId(anyInt()))
                .thenReturn(Optional.of(shelfDb));

        BookLocation bookLocation = bookService.findBookLocation(1);

        assertEquals(shelfDb.getShelfNumber(), bookLocation.shelfNumber());
        assertEquals(shelfDb.getFloor(), bookLocation.floor());
    }

    private Shelf getShelf() {
        return Shelf.builder()
                .books(Set.of(getBook()))
                .floor(0)
                .id(1)
                .shelfNumber(1)
                .build();
    }

    @DisplayName("Create and save requested book")
    @Test
    void requestBook() {
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        User user = new User();

        ArgumentCaptor<RequestedBook> acRequestedBook = ArgumentCaptor.forClass(RequestedBook.class);

        when(userService.getUser(anyString()))
                .thenReturn(user);

        bookService.requestBook("username", requestedBookDTO);

        verify(requestedBookRepository, times(1))
                .save(acRequestedBook.capture());

        RequestedBook requestedBookToSave = acRequestedBook.getValue();

        assertEquals(requestedBookDTO.bookTitle(), requestedBookToSave.getBookTitle());
        assertEquals(requestedBookDTO.author(), requestedBookToSave.getAuthor());
        assertEquals(requestedBookDTO.additionalInformation(), requestedBookToSave.getAdditionalInformation());
        assertEquals(user, requestedBookToSave.getUser());
    }

    private RequestedBookDTO getRequestedBookDTO() {
        return new RequestedBookDTO("title", "author", "information");
    }

    @DisplayName("Create book - Category not found")
    @Test
    void createBookCategoryNotFound() {
        BookRequest bookRequest = getBookRequest();

        when(categoryRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                bookService.createBook(bookRequest));
    }

    @DisplayName("Create book - Shelf not found")
    @Test
    void createBookShelfNotFound() {
        BookRequest bookRequest = getBookRequest();

        Category bookCategory = new Category();

        when(categoryRepository.findById(anyInt()))
                .thenReturn(Optional.of(bookCategory));

        when(shelfRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                bookService.createBook(bookRequest));
    }

    @DisplayName("Create book")
    @Test
    void createBook() {
        BookRequest bookRequest = getBookRequest();

        Category bookCategory = new Category();

        Shelf shelf = new Shelf();

        ArgumentCaptor<Book> acBook = ArgumentCaptor.forClass(Book.class);

        when(categoryRepository.findById(anyInt()))
                .thenReturn(Optional.of(bookCategory));

        when(shelfRepository.findById(anyInt()))
                .thenReturn(Optional.of(shelf));

        bookService.createBook(bookRequest);

        verify(bookRepository, times(1))
                .save(acBook.capture());

        Book book = acBook.getValue();

        assertNotNull(book);
        assertTrue(book.getAvailable());
        assertNotNull(book.getCategory());
        assertNotNull(book.getShelf());
    }

    private BookRequest getBookRequest() {
        return new BookRequest("title", "author", "publisher", LocalDate.now(),
                1, 1);
    }

    @DisplayName("Update book property available and save")
    @Test
    void updateBookAvailable() {
        Book bookToUpdate = new Book();

        ArgumentCaptor<Book> acBook = ArgumentCaptor.forClass(Book.class);

        bookService.updateBookAvailable(true, bookToUpdate);

        verify(bookRepository, times(1))
                .save(acBook.capture());

        Book savedBook = acBook.getValue();
        assertTrue(savedBook.getAvailable());
    }
}