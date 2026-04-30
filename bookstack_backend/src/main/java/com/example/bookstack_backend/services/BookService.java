package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.Review;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.BookRepository;
import com.example.bookstack_backend.repository.ReviewRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Book createBookWithCover(CreateBookRequest request, MultipartFile cover) {
            // 1. Validate User
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getOwnerId()));

            // 2. Map request to Book entity
            Book newBook = new Book(
                    owner,
                    request.getStock(),
                    request.getPrice(),
                    request.getTitle(),
                    request.getAuthor(),
                    request.getCondition(),
                    request.getIsbn()
            );

            newBook.setGenre(request.getGenre());
            newBook.setEdition(request.getEdition());
            newBook.setPublishedYear(request.getPublishedYear());
            newBook.setDescription(request.getDescription());
            newBook.setIsActive(false); // Assuming books need admin approval
            newBook.setDatePosted(LocalDateTime.now());

            // 3. Handle File Upload (Directly into the entity)
            if (cover != null && !cover.isEmpty()) {
                try {
                    newBook.setCoverImage(cover.getBytes());
                    newBook.setCoverImageType(cover.getContentType());
                    // Optional: You can still set a URL if you have a specific endpoint to serve these bytes
                    // newBook.setCoverImageUrl("/api/books/image/" + someIdentifier);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read image bytes", e);
                }
            }

            // 4. Save once. The ID and bytes are persisted together.
            return bookRepository.save(newBook);
        }

    public Book createBookListing(CreateBookRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getOwnerId()));

        Book newBook = new Book(
                owner,
                request.getStock(),
                request.getPrice(),
                request.getTitle(),
                request.getAuthor(),
                request.getCondition(),
                request.getIsbn()
        );

        newBook.setGenre(request.getGenre());
        newBook.setEdition(request.getEdition());
        newBook.setPublishedYear(request.getPublishedYear());
        newBook.setDescription(request.getDescription());
        newBook.setCoverImageUrl(request.getCoverImageUrl());

        return bookRepository.save(newBook);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findByIsActiveTrue();
    }

    public List<BookResponse> getBooksByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + ownerId));

        List<Book> books = bookRepository.findByOwner(owner);

        return books.stream().map(BookResponse::new).collect(Collectors.toList());
    }


    public Book findBookById(Long id) {
        return bookRepository.findByBookIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public void updateCover(Long id, byte[] imageBytes, String contentType) {
        Book book = findBookByIdIgnoreActive(id);
        book.setCoverImage(imageBytes);
        book.setCoverImageType(contentType);
        bookRepository.save(book);
    }
    
    public Book updateBookListing(Long bookId, CreateBookRequest updateRequest) {
        Book existingBook = findBookByIdIgnoreActive(bookId);

        existingBook.setTitle(updateRequest.getTitle());
        existingBook.setAuthor(updateRequest.getAuthor());
        existingBook.setIsbn(updateRequest.getIsbn());
        existingBook.setGenre(updateRequest.getGenre());
        existingBook.setEdition(updateRequest.getEdition());
        existingBook.setPublishedYear(updateRequest.getPublishedYear());
        existingBook.setPrice(updateRequest.getPrice());
        existingBook.setStock(updateRequest.getStock());
        existingBook.setCondition(updateRequest.getCondition());
        existingBook.setDescription(updateRequest.getDescription());
        
        if (updateRequest.getCoverImageUrl() != null && !updateRequest.getCoverImageUrl().isEmpty()) {
            existingBook.setCoverImageUrl(updateRequest.getCoverImageUrl());
        }
        existingBook.setIsActive(false);
        return bookRepository.save(existingBook);
    }

    public void deleteBookListing(Long bookId) {
        Book existingBook = findBookById(bookId);

        bookRepository.delete(existingBook);
    }

    public List<Book> getSimilarBooks(Long bookId) {
    Book target = bookRepository.findById(bookId)
        .orElseThrow(() -> new RuntimeException("Book not found"));

    return bookRepository.findByGenreAndBookIdNot(target.getGenre(), bookId);
    }

    public List<Book> searchBooks(String query, Double minPrice, Double maxPrice, Integer minYear, Integer maxYear) {
        List<Book> baseResults = bookRepository.searchAllBooks(query);
    
    
        return baseResults.stream()
                .filter(book -> minPrice == null || book.getPrice().compareTo(BigDecimal.valueOf(minPrice)) >= 0)
                .filter(book -> maxPrice == null || book.getPrice().compareTo(BigDecimal.valueOf(maxPrice)) <= 0)
                .filter(book -> minYear == null || book.getPublishedYear() >= minYear)
                .filter(book -> maxYear == null || book.getPublishedYear() <= maxYear)
                .toList();
    }

    public Book findBookByIdIgnoreActive(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public void updateReview(Long bookId, User user, int rating) {
        Book book = findBookById(bookId);

        if (reviewRepository.existsByUserAndBook(user, book)) {
            throw new IllegalStateException("You have already reviewed this book");
        }

        reviewRepository.save(new Review(user, book, rating));
        book.setReviewCount(book.getReviewCount() + 1);
        book.setTotalScore(book.getTotalScore() + rating);
        bookRepository.save(book);
    }

}
