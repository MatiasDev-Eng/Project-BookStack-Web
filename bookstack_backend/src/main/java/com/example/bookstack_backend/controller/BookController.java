package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

import java.util.logging.Logger;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@EnableAsync
@RequestMapping("/api/books/")
public class BookController {

//    @Autowired
//    private BookRepository bookRepository;
//
//    private static final Logger logger = Logger.getLogger(BookController.class.getName());
//
//    // create book
//    @PostMapping("/")
//    @Operation(
//            summary = "Creates book entry",
//            description = "Creates book object, defaults to pending until admin approves / rejects",
//            tags = { "books", "post" })
//    public ResponseEntity<?> createBook(@Valid @RequestBody CreateBookRequest request) {
//        return ResponseEntity.ok().build();
//    }
//
//    // find by book id
//    @GetMapping("/{bookId}")
//    @Operation(
//            summary = "Finds book by id",
//            description = "Accepts Id param, queries database for that book's info",
//            tags = { "books", "get" })
//    public ResponseEntity<?> findBookById(@PathVariable Long bookId) {
//        return ResponseEntity.ok().build();
//    }
//
//    // find by title
//    @GetMapping("/{bookTitle}/")
//    @Operation(
//            summary = "Finds book by title",
//            description = "Accepts title param, queries database for that book's info",
//            tags = { "books", "get" })
//    public ResponseEntity<?> findBookByTitle(@PathVariable String title) {
//        return ResponseEntity.ok().build();
//    }
//
//    // find by author
//    @GetMapping("/{bookAuthor}/")
//    @Operation(
//            summary = "Finds book(s) by author",
//            description = "Accepts author param, queries database for books by that author",
//            tags = { "books", "get" })
//    public ResponseEntity<?> findBookByAuthor(@PathVariable String author) {
//        return ResponseEntity.ok().build();
//    }
//
//    // find by year
//    //
}
