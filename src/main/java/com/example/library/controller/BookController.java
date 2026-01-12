package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.entity.Author;
import com.example.library.entity.Genre;
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

    // 1. СПИСОК ВСЕХ КНИГ
    @GetMapping
    public String listBooks(Model model) {
        try {
            List<Book> books = bookService.getAllBooks();
            model.addAttribute("books", books);
            model.addAttribute("bookCount", books.size());
        } catch (Exception e) {
            log.error("Ошибка при получении списка книг", e);
            model.addAttribute("error", "Ошибка при загрузке списка книг: " + e.getMessage());
        }
        return "books/list";
    }

    // 2. ФОРМА ДОБАВЛЕНИЯ/РЕДАКТИРОВАНИЯ
    @GetMapping("/form")
    public String showBookForm(@RequestParam(required = false) Long id, Model model) {
        try {
            if (id != null && id > 0) {
                // Редактирование существующей книги
                Book book = bookService.getBookById(id);
                model.addAttribute("book", book);
            } else {
                // Создание новой книги
                model.addAttribute("book", new Book());
            }

            // Получаем списки авторов и жанров
            List<Author> authors = authorService.getAllAuthors();
            List<Genre> genres = genreService.getAllGenres();

            // Проверяем доступность данных
            if (authors.isEmpty()) {
                model.addAttribute("warning", "Сначала добавьте автора в системе");
            }

            if (genres.isEmpty()) {
                model.addAttribute("warning", "Сначала добавьте жанр в системе");
            }

            model.addAttribute("authors", authors);
            model.addAttribute("genres", genres);

        } catch (Exception e) {
            log.error("Ошибка при загрузке формы книги", e);
            model.addAttribute("error", "Ошибка при загрузке формы: " + e.getMessage());
            return "redirect:/books";
        }
        return "books/form";
    }

    // 3. СОХРАНИТЬ/ОБНОВИТЬ КНИГУ (УЛУЧШЕННАЯ ВЕРСИЯ)
    @PostMapping("/save")
    public String saveBook(
            @RequestParam(required = false) Long id,
            @RequestParam String title,
            @RequestParam Long authorId,
            @RequestParam Long genreId,
            @RequestParam Integer publicationYear,
            @RequestParam String isbn,
            @RequestParam(required = false, defaultValue = "0") Integer pages,
            @RequestParam(required = false, defaultValue = "0") Integer quantity,
            @RequestParam(required = false, defaultValue = "") String description,
            Model model) {

        try {
            log.info("Сохранение книги: title={}, authorId={}, genreId={}",
                    title, authorId, genreId);

            Book book;
            if (id != null && id > 0) {
                // Редактирование существующей книги
                book = bookService.getBookById(id);
                log.info("Редактирование книги с id={}", id);
            } else {
                // Создание новой книги
                book = new Book();
                log.info("Создание новой книги");
            }

            // Валидация обязательных полей
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Название книги обязательно");
            }
            if (authorId == null || authorId <= 0) {
                throw new IllegalArgumentException("Необходимо выбрать автора");
            }
            if (genreId == null || genreId <= 0) {
                throw new IllegalArgumentException("Необходимо выбрать жанр");
            }
            if (publicationYear == null || publicationYear < 1000 || publicationYear > 2100) {
                throw new IllegalArgumentException("Некорректный год публикации");
            }
            if (isbn == null || isbn.trim().isEmpty()) {
                throw new IllegalArgumentException("ISBN обязателен");
            }

            // Заполняем поля
            book.setTitle(title.trim());
            book.setPublicationYear(publicationYear);
            book.setIsbn(isbn.trim());
            book.setPages(pages != null ? pages : 0);
            book.setQuantity(quantity != null ? quantity : 0);
            book.setDescription(description != null ? description.trim() : "");

            // Получаем автора
            Author author = authorService.getAuthorById(authorId);
            if (author == null) {
                throw new IllegalArgumentException("Автор с id=" + authorId + " не найден");
            }
            book.setAuthor(author);

            // Получаем жанр
            Genre genre = genreService.getGenreById(genreId);
            if (genre == null) {
                throw new IllegalArgumentException("Жанр с id=" + genreId + " не найден");
            }
            book.setGenre(genre);

            // Сохраняем книгу
            Book savedBook = bookService.saveBook(book);
            log.info("Книга успешно сохранена с id={}", savedBook.getId());

            return "redirect:/books?success";

        } catch (Exception e) {
            log.error("Ошибка при сохранении книги", e);

            // Возвращаем на форму с сообщением об ошибке
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("bookTitle", title);
            model.addAttribute("selectedAuthorId", authorId);
            model.addAttribute("selectedGenreId", genreId);
            model.addAttribute("publicationYear", publicationYear);
            model.addAttribute("isbn", isbn);
            model.addAttribute("pages", pages);
            model.addAttribute("quantity", quantity);
            model.addAttribute("description", description);

            // Загружаем списки для формы
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getAllGenres());

            return "books/form";
        }
    }

    // 4. УДАЛИТЬ КНИГУ
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

    // 5. ПРОСМОТР КНИГИ
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