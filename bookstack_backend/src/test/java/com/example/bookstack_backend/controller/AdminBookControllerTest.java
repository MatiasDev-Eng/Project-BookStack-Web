package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.services.AdminBookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminBookService adminBookService;

    @Test
    void testGetAllBooks() throws Exception {
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Admin View Book");

        when(adminBookService.getAllBooks()).thenReturn(Arrays.asList(book));

        mockMvc.perform(get("/admin/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Admin View Book"));
    }

    @Test
    void testGetBooksByOwner() throws Exception {
        Book book = new Book();
        book.setBookId(1L);

        when(adminBookService.getBooksByOwner(1L)).thenReturn(Arrays.asList(book));

        mockMvc.perform(get("/admin/books/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testDeleteBook() throws Exception {
        Mockito.doNothing().when(adminBookService).deleteBook(1L);

        mockMvc.perform(delete("/admin/books/1"))
                .andExpect(status().isOk());
    }
}
