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
}
