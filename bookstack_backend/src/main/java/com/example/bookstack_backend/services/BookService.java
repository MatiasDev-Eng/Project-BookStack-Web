package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.BookRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Book> getActiveBooksByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + ownerId));
        return bookRepository.findByOwnerAndIsActiveTrue(owner);
    }

    public Book getBookById(Long bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("The provided Book ID cannot be null.");
        }

        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));
    }
    
    public Book updateBookListing(Long bookId, CreateBookRequest updateRequest) {
        Book existingBook = getBookById(bookId);

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
        Book existingBook = getBookById(bookId);

        bookRepository.delete(existingBook);
    }

    
}
