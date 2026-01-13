package com.example.library.repository;

import com.example.library.entity.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByIsbnContainingIgnoreCase(String isbn);

    @Query("SELECT b FROM Book b WHERE LOWER(b.author.firstName) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "OR LOWER(b.author.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Book> findByAuthorNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT b FROM Book b WHERE LOWER(b.genre.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Book> findByGenreNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT b FROM Book b WHERE "
            + "LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) OR "
            + "LOWER(b.author.firstName) LIKE LOWER(CONCAT('%', :author, '%')) OR "
            + "LOWER(b.author.lastName) LIKE LOWER(CONCAT('%', :author, '%')) OR "
            + "LOWER(b.genre.name) LIKE LOWER(CONCAT('%', :genre, '%')) OR "
            + "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%'))")
    List<Book> findByTitleContainingIgnoreCaseOrAuthorNameContainingIgnoreCaseOrGenreNameContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            @Param("title") String title,
            @Param("author") String author,
            @Param("genre") String genre,
            @Param("isbn") String isbn);
}