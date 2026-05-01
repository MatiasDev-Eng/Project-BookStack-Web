package com.example.bookstack_backend.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByOwner(User owner);
    List<Book> findByIsActiveFalse();
    List<Book> findByIsFrozenTrueAndIsDeletedFalse();
    List<Book> findByIsActiveTrueAndIsDeletedFalse();
    List<Book> findByIsActiveTrueAndIsFrozenFalseAndIsDeletedFalse();
    List<Book> findByGenreAndBookIdNot(String genre, Long bookId);
    Optional<Book> findByBookIdAndIsActiveTrueAndIsDeletedFalse(Long bookId);

    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.genre) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND b.isActive = true AND b.isFrozen = false AND b.isDeleted = false")
    List<Book> searchActiveBooks(@Param("query") String query);

    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.genre) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND b.isFrozen = false AND b.isDeleted = false")
    List<Book> searchAllBooks(@Param("query") String query);

    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.isActive = true WHERE b.bookId = :bookId")
    void approveBook(@Param("bookId") Long bookId);
}
