package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.services.AdminBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/books")
@CrossOrigin(origins = "*")
public class AdminBookController {
    @Autowired
    private AdminBookService adminBookService;

    // GET all books
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<Book> bookList = adminBookService.getAllBooks();
        List<BookResponse> responseList = bookList.stream()
                .map(BookResponse::new)
                .toList();
        return ResponseEntity.ok(responseList);
    }

    // GET books by owner
    @GetMapping("/owner/{ownerId}")
    public List<Book> getBooksByOwner(@PathVariable Long ownerId) {
        return adminBookService.getBooksByOwner(ownerId);
    }

    // DELETE book
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        adminBookService.deleteBook(id);
    }
}