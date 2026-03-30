package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.BookRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
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
        return bookRepository.findAll();
    }

    public List<BookResponse> getBooksByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + ownerId));

        List<Book> books = bookRepository.findByOwner(owner);

        return books.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookResponse mapToResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getBookId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setGenre(book.getGenre());
        response.setPrice(book.getPrice());
        return response;
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
    
    public Book updateBookListing(Long bookId, CreateBookRequest updateRequest) {
        Book existingBook = findBookById(bookId);

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

    public List<Book> searchBooks(String query) {
    return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(query, query, query);
    }


}
