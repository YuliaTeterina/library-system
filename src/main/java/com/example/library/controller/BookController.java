package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.BookService;
import com.example.library.service.AuthorService;
import com.example.library.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String search,
            Model model) {
        try {
            List<Book> books;
            if (search != null && !search.trim().isEmpty()) {
                books = bookService.searchBooks(search);
                model.addAttribute("search", search);
            } else {
                books = bookService.getAllBooks();
            }
            model.addAttribute("books", books);
            model.addAttribute("bookCount", books.size());
        } catch (Exception e) {
            log.error("Ошибка при получении списка книг", e);
            model.addAttribute("error", "Ошибка при загрузке списка книг: " + e.getMessage());
        }
        return "books/list";
    }

    @GetMapping("/form")
    public String showBookForm(@RequestParam(required = false) Long id, Model model) {
        try {
            if (id != null && id > 0) {
                Book book = bookService.getBookById(id);
                model.addAttribute("book", book);
            } else {
                model.addAttribute("book", new Book());
            }

            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getAllGenres());

        } catch (Exception e) {
            log.error("Ошибка при загрузке формы книги", e);
            model.addAttribute("error", "Ошибка при загрузке формы: " + e.getMessage());
            return "redirect:/books";
        }
        return "books/form";
    }

    @PostMapping("/save")
    public String saveBook(
            @RequestParam(required = false) Long id,
            @RequestParam String title,
            @RequestParam Long authorId,
            @RequestParam Long genreId,
            @RequestParam Integer publicationYear,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false, defaultValue = "0") Integer pages,
            @RequestParam(required = false, defaultValue = "0") Integer quantity,
            @RequestParam(required = false, defaultValue = "") String description,
            Model model) {

        try {
            Book book;
            if (id != null && id > 0) {
                book = bookService.getBookById(id);
            } else {
                book = new Book();
            }

            book.setTitle(title.trim());
            book.setPublicationYear(publicationYear);
            book.setIsbn(isbn != null ? isbn.trim() : "");
            book.setPages(pages != null ? pages : 0);
            book.setQuantity(quantity != null ? quantity : 0);
            book.setDescription(description != null ? description.trim() : "");

            book.setAuthor(authorService.getAuthorById(authorId));
            book.setGenre(genreService.getGenreById(genreId));

            bookService.saveBook(book);
            return "redirect:/books?success";

        } catch (Exception e) {
            log.error("Ошибка при сохранении книги", e);
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("bookTitle", title);
            model.addAttribute("selectedAuthorId", authorId);
            model.addAttribute("selectedGenreId", genreId);
            model.addAttribute("publicationYear", publicationYear);
            model.addAttribute("isbn", isbn);
            model.addAttribute("pages", pages);
            model.addAttribute("quantity", quantity);
            model.addAttribute("description", description);
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getAllGenres());
            return "books/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return "redirect:/books?deleted";
        } catch (Exception e) {
            log.error("Ошибка при удалении книги с id={}", id, e);
            return "redirect:/books?error=Ошибка при удалении книги";
        }
    }

    @GetMapping("/view/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
        } catch (Exception e) {
            log.error("Ошибка при просмотре книги с id={}", id, e);
            model.addAttribute("error", "Книга не найдена: " + e.getMessage());
            return "redirect:/books";
        }
        return "books/view";
    }
}