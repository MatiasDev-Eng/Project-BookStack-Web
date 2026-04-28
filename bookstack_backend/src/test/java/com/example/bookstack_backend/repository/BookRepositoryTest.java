package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.models.ECondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByOwner() {
        User user = new User("test", "test@mail.com", "pass");
        userRepository.save(user);

        Book book = new Book(user, 10, BigDecimal.TEN, "Title", "Author", ECondition.NEW, "123");
        bookRepository.save(book);

        List<Book> found = bookRepository.findByOwner(user);
        assertFalse(found.isEmpty());
        assertEquals("Title", found.get(0).getTitle());
    }

    @Test
    void testSearchByTitleOrAuthorOrGenre() {
        User user = new User("test", "test@mail.com", "pass");
        userRepository.save(user);

        Book book = new Book(user, 10, BigDecimal.TEN, "UniqueTitle", "UniqueAuthor", ECondition.NEW, "123");
        book.setGenre("UniqueGenre");
        bookRepository.save(book);

        List<Book> found = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
                "uniquetitle", "nothing", "nothing"
        );
        assertFalse(found.isEmpty());

        found = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
                "nothing", "uniqueauthor", "nothing"
        );
        assertFalse(found.isEmpty());

        found = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
                "nothing", "nothing", "uniquegenre"
        );
        assertFalse(found.isEmpty());
    }

    @Test
    void testApproveBook() {
        User user = new User("test", "test@mail.com", "pass");
        userRepository.save(user);

        Book book = new Book(user, 10, BigDecimal.TEN, "Title", "Author", ECondition.NEW, "123");
        book.setIsActive(false);
        bookRepository.save(book);

        bookRepository.approveBook(book.getBookId());
        
        // Refresh from DB
        Book updated = bookRepository.findById(book.getBookId()).orElseThrow();
        assertTrue(updated.getIsActive());
    }
}
