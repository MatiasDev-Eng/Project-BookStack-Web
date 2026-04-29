package com.example.bookstack_backend.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByOwner(User owner);
    List<Book> findByIsActiveFalse();
    List<Book> findByGenreAndBookIdNot(String genre, Long bookId);
    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.genre) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND b.isActive = true")
    List<Book> searchActiveBooks(@Param("query") String query);    List<Book> findByIsActiveTrue();
    Optional<Book> findByBookIdAndIsActiveTrue(Long bookId);

    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.genre) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Book> searchAllBooks(@Param("query") String query);


    @Modifying
    @Transactional
    @Query("UPDATE Book u SET u.isActive = true WHERE u.id = :bookId")
    void approveBook(@Param("bookId") Long bookId);
//    List<Book> findByPrice(Integer price);
//    List<Book> findByTitleContaining(String title);
//    List<Book> findByAuthor(String author);
//    List<Book> findByYear(Integer year);
}
