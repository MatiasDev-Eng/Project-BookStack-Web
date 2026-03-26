package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@EnableAsync
@RequestMapping("/api/books/")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
            summary = "Create book listing",
            description = "(TAKES IN JWT) Creates an entry in the book table. Books won't be visible until admin approves",
            tags = { "books", "post" })
    @PostMapping("/")
    public ResponseEntity<Book> addBookListing(@RequestBody CreateBookRequest request) {
        Book savedBook = bookService.createBookListing(request);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Lists all books",
            description = "(TAKES IN JWT) Returns metadata + download link for all pcls owned by a user",
            tags = { "pointclouds", "get" })
    @GetMapping("/")
    public ResponseEntity<List<BookResponse>> getAllPointclouds() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Book> bookList = bookService.getAllBooks();

        List<BookResponse> responseList = bookList.stream()
                .map(book -> {
                    return new BookResponse(book);
                })
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Book>> getSellerListings(@PathVariable Long ownerId) {
        List<Book> listings = bookService.getActiveBooksByOwner(ownerId);
        
        return new ResponseEntity<>(listings, HttpStatus.OK);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getSingleBook(@PathVariable Long bookId) {
        Book book = bookService.findBookById(bookId);
        // Convert the Entity to a clean Response object
        return ResponseEntity.ok(new BookResponse(book));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBookListing(
            @PathVariable Long bookId,
            @RequestBody CreateBookRequest updateRequest) {

        Book updatedBook = bookService.updateBookListing(bookId, updateRequest);

        return ResponseEntity.ok(new BookResponse(updatedBook));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBookListing(@PathVariable Long bookId) {
        
        bookService.deleteBookListing(bookId);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{bookId}/similar")
    public ResponseEntity<List<BookResponse>> getSimilarBooks(@PathVariable Long bookId) {
        List<Book> similar = bookService.getSimilarBooks(bookId);

        List<BookResponse> response = similar.stream()
            .map(BookResponse::new)
            .toList();

        return ResponseEntity.ok(response);
    }

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
