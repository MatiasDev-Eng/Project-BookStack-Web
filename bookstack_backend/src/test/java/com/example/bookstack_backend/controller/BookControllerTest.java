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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private BookResponse response;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("testuser");
        response.setAuthor("testauthor");
        response.setBookId(1L);
        response.setGenre("genre");
        response.setIsbn("isbn");
        response.setTitle("title");
        response.setPrice(BigDecimal.TEN);

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

    @Test
    void testGetSingleBook_Success() throws Exception {
        when(bookService.findBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void testUpdateBookListing_Success() throws Exception {
        CreateBookRequest updateRequest = new CreateBookRequest();
        updateRequest.setTitle("Updated Title");
        
        when(bookService.updateBookListing(eq(1L), any(CreateBookRequest.class))).thenReturn(book);
        book.setTitle("Updated Title");

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testDeleteBookListing_Success() throws Exception {
        Mockito.doNothing().when(bookService).deleteBookListing(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetSellerListings_Success() throws Exception {
        when(bookService.getBooksByOwner(1L)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/books/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetSimilarBooks_Success() throws Exception {
        when(bookService.getSimilarBooks(1L)).thenReturn(Arrays.asList(book));

        mockMvc.perform(get("/api/books/1/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testSearchByTextQuery_Success() throws Exception {
        // Tests REQ-8, REQ-9, and the basic query part of REQ-16
        mockMvc.perform(get("/api/books/search")
                        .param("query", "The Great Gatsby"))
                .andExpect(status().isOk());
    }
}
