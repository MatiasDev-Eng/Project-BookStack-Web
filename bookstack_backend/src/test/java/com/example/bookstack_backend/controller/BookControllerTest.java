package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.ECondition;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple unit test
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("testuser");

        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPrice(new BigDecimal("29.99"));
        book.setIsbn("1234567890");
        book.setOwner(owner);
        book.setStock(20);
        book.setGenre("Mistery");
        book.setEdition("1st");
        book.setCondition(ECondition.NEW);
        book.setReviewCount(0);
        book.setTotalScore(0.f);
        book.setPublishedYear(2000);
        book.setDescription("test book");
        book.setIsActive(true);
        book.setCoverImageUrl("");
        book.setDatePosted(LocalDateTime.now());

        // test args constructor
        Book testBook = new Book(owner, 20, BigDecimal.valueOf(20.f), "pride", "test", ECondition.ACCEPTABLE, "isbn");
    }

    @Test
    void testAddBookListing_ValidRequest() throws Exception {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("New Book");
        request.setAuthor("New Author");
        request.setPrice(new BigDecimal("19.99"));
        request.setCondition(ECondition.NEW);

        when(bookService.createBookListing(any(CreateBookRequest.class))).thenReturn(book);

        mockMvc.perform(post("/api/books/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllBooks_Success() throws Exception {
        // Mock SecurityContext
        UserDetailsImpl userDetails = UserDetailsImpl.build(owner);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        List<Book> books = Arrays.asList(book);
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllBooks_EmptyList() throws Exception {
        // Mock SecurityContext
        UserDetailsImpl userDetails = UserDetailsImpl.build(owner);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
