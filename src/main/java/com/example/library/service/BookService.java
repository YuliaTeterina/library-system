package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена с id: " + id));
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        String searchTerm = keyword.trim();
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorNameContainingIgnoreCaseOrGenreNameContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm, searchTerm);
    }
}