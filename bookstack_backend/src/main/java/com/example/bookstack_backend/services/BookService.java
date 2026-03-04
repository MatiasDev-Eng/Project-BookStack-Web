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
}
