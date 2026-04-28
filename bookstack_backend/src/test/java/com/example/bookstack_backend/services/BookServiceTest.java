package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.ECondition;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.BookRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    private User user;
    private CreateBookRequest createBookRequest;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        createBookRequest = new CreateBookRequest();
        createBookRequest.setOwnerId(1L);
        createBookRequest.setTitle("Test Book");
        createBookRequest.setAuthor("Test Author");
        createBookRequest.setPrice(BigDecimal.valueOf(19.99));
        createBookRequest.setStock(10);
        createBookRequest.setCondition(ECondition.NEW);
        createBookRequest.setIsbn("1234567890");
    }

    @Test
    void createBookListing_ShouldCreateBook_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book createdBook = bookService.createBookListing(createBookRequest);

        assertNotNull(createdBook);
        assertEquals("Test Book", createdBook.getTitle());
        assertEquals(user, createdBook.getOwner());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBookListing_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.createBookListing(createBookRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        Book book1 = new Book();
        Book book2 = new Book();
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBooksByOwner_ShouldReturnBooks_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Book book = new Book();
        book.setBookId(1L);
        book.setOwner(user);
        when(bookRepository.findByOwner(user)).thenReturn(Arrays.asList(book));

        List<com.example.bookstack_backend.dto.response.BookResponse> result = bookService.getBooksByOwner(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getBookId());
    }

    @Test
    void findBookById_ShouldReturnBook_WhenExists() {
        Book book = new Book();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.findBookById(1L);

        assertEquals(book, result);
    }

    @Test
    void updateCover_ShouldUpdateBook() {
        Book book = new Book();
        byte[] image = "image".getBytes();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.updateCover(1L, image, "image/png");

        assertArrayEquals(image, book.getCoverImage());
        assertEquals("image/png", book.getCoverImageType());
        verify(bookRepository).save(book);
    }

    @Test
    void updateBookListing_ShouldUpdateFields() {
        Book book = new Book();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        createBookRequest.setTitle("New Title");
        Book updated = bookService.updateBookListing(1L, createBookRequest);

        assertEquals("New Title", updated.getTitle());
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBookListing_ShouldDelete() {
        Book book = new Book();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBookListing(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void getSimilarBooks_ShouldReturnBooks() {
        Book book = new Book();
        book.setGenre("Sci-Fi");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByGenreAndBookIdNot("Sci-Fi", 1L)).thenReturn(Arrays.asList(new Book()));

        List<Book> result = bookService.getSimilarBooks(1L);

        assertEquals(1, result.size());
    }

    @Test
    void searchBooks_ShouldFilterResults() {
        Book book1 = new Book();
        book1.setPrice(BigDecimal.valueOf(10));
        book1.setPublishedYear(2020);
        
        Book book2 = new Book();
        book2.setPrice(BigDecimal.valueOf(100));
        book2.setPublishedYear(2010);

        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(anyString(), anyString(), anyString()))
                .thenReturn(Arrays.asList(book1, book2));

        List<Book> result = bookService.searchBooks("query", 5.0, 50.0, 2015, 2025);

        assertEquals(1, result.size());
        assertEquals(book1, result.get(0));
    }
}
