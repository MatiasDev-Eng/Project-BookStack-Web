package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.EActionType;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.UserRepository;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.AuditLogService;
import com.example.bookstack_backend.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@EnableAsync
@RequestMapping("/api/books/")
public class BookController {

    private final BookService bookService;

    @Autowired
    AuditLogService logService;

    @Autowired
    private UserRepository userRepository;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
            summary = "Create book listing with cover",
            description = "(TAKES IN JWT) Creates book and uploads cover in one transaction.",
            tags = { "books", "post" })
    @PostMapping(value = "/", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<BookResponse> addBookListing(
            @RequestPart("book") CreateBookRequest request,
            @RequestPart(value = "cover", required = false) MultipartFile cover) {

        // 1. Pass both the request and the file to your service
        // 2. The service should save the book, upload the file, then update the book's image URL
        Book savedBook = bookService.createBookWithCover(request, cover);

        logService.log(savedBook.getOwner().getId(), EActionType.BOOK_POSTED, "Book: " + savedBook.getTitle());

        return new ResponseEntity<>(new BookResponse(savedBook), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Lists all books",
            description = "(TAKES IN JWT) Returns metadata + download link for all pcls owned by a user",
            tags = { "pointclouds", "get" })
    @GetMapping("/")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
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

    @GetMapping("/me/")
    public ResponseEntity<List<BookResponse>> getSellerListings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<BookResponse> listings = bookService.getBooksByOwner(userDetails.getId());
        
        return new ResponseEntity<>(listings, HttpStatus.OK);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getSingleBook(@PathVariable Long bookId) {
        Book book = bookService.findBookByIdIgnoreActive(bookId);  // ← changed from findBookById
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

    @Operation(summary = "Review a book", tags = {"books", "post"})
    @PostMapping("/{bookId}/review")
    public ResponseEntity<?> reviewBook(@PathVariable Long bookId,
                                        @RequestParam int rating) {
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            bookService.updateReview(bookId, user, rating);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        Book updatedBook = bookService.findBookById(bookId);
        float avg = updatedBook.getTotalScore() / updatedBook.getReviewCount();
        return ResponseEntity.ok(Map.of(
                "reviewCount", updatedBook.getReviewCount(),
                "averageRating", Math.round(avg * 10.0) / 10.0
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
        @RequestParam String query,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) Integer minYear,
        @RequestParam(required = false) Integer maxYear
        
    ) {
        List<Book> results = bookService.searchBooks(query, minPrice, maxPrice, minYear, maxYear);

        List<BookResponse> response = results.stream()
                .map(BookResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cover")
    public ResponseEntity<?> uploadCover(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("File must be an image.");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("Image must be under 5 MB.");
        }

        try {
            bookService.updateCover(id, file.getBytes(), contentType);
            return ResponseEntity.ok("Cover uploaded.");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to read file.");
        }
    }

    @GetMapping("/{id}/cover")
    public ResponseEntity<byte[]> getCover(@PathVariable Long id) {
        try {
            Book book = bookService.findBookByIdIgnoreActive(id);  // ← changed from findBookById
            if (book.getCoverImage() == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(book.getCoverImageType()))
                    .body(book.getCoverImage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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
