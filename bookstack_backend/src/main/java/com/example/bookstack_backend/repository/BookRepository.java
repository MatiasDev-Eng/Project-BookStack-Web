package com.example.bookstack_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookstack_backend.models.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByPrice(Integer price);
    List<Book> findByTitleContaining(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByYear(Integer year);
}
