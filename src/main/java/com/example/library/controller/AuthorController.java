package com.example.library.controller;

import com.example.library.entity.Author;
import com.example.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    // 1. СПИСОК ВСЕХ АВТОРОВ
    @GetMapping
    public String listAuthors(Model model) {
        try {
            log.debug("Получение списка авторов");
            List<Author> authors = authorService.getAllAuthors();
            model.addAttribute("authors", authors);
            model.addAttribute("authorCount", authors.size());

            // Проверяем параметры URL для сообщений
            if (model.containsAttribute("successMessage")) {
                model.addAttribute("success", model.getAttribute("successMessage"));
            }
            if (model.containsAttribute("errorMessage")) {
                model.addAttribute("error", model.getAttribute("errorMessage"));
            }

        } catch (Exception e) {
            log.error("Ошибка при получении списка авторов", e);
            model.addAttribute("error", "Не удалось загрузить список авторов: " + e.getMessage());
        }
        return "authors/list";
    }

    // 2. ФОРМА ДОБАВЛЕНИЯ/РЕДАКТИРОВАНИЯ АВТОРА
    @GetMapping("/form")
    public String showAuthorForm(@RequestParam(required = false) Long id, Model model) {
        try {
            log.debug("Открытие формы автора, id={}", id);

            if (id != null && id > 0) {
                // Редактирование существующего автора
                Author author = authorService.getAuthorById(id);
                model.addAttribute("author", author);
                log.debug("Редактирование автора: {}", author.getFullName());
            } else {
                // Создание нового автора
                model.addAttribute("author", new Author());
                log.debug("Создание нового автора");
            }

        } catch (RuntimeException e) {
            log.error("Автор с id={} не найден", id, e);
            model.addAttribute("error", "Автор не найден");
            return "redirect:/authors";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы автора", e);
            model.addAttribute("error", "Ошибка при загрузке формы: " + e.getMessage());
            return "redirect:/authors";
        }
        return "authors/form";
    }

    // 3. СОЗДАНИЕ НОВОГО АВТОРА
    @PostMapping
    public String createAuthor(@Valid @ModelAttribute Author author,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        log.debug("Создание автора: {}", author.getFullName());

        // Валидация
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании автора: {}", bindingResult.getAllErrors());
            return "authors/form";
        }

        try {
            // Проверка уникальности (можно добавить кастомную проверку)
            Author savedAuthor = authorService.saveAuthor(author);
            log.info("Автор успешно создан: {} (id={})", savedAuthor.getFullName(), savedAuthor.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Автор успешно добавлен");
            return "redirect:/authors";

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности данных при создании автора", e);
            model.addAttribute("error", "Ошибка: возможно, автор с такими данными уже существует");
            return "authors/form";

        } catch (Exception e) {
            log.error("Неизвестная ошибка при создании автора", e);
            model.addAttribute("error", "Ошибка при сохранении автора: " + e.getMessage());
            return "authors/form";
        }
    }

    // 4. ОБНОВЛЕНИЕ СУЩЕСТВУЮЩЕГО АВТОРА
    @PostMapping("/update/{id}")
    public String updateAuthor(@PathVariable Long id,
                               @Valid @ModelAttribute Author author,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        log.debug("Обновление автора с id={}", id);

        // Валидация
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении автора: {}", bindingResult.getAllErrors());
            return "authors/form";
        }

        try {
            // Проверяем, существует ли автор
            authorService.getAuthorById(id);

            // Устанавливаем ID (важно!)
            author.setId(id);

            // Сохраняем
            Author savedAuthor = authorService.saveAuthor(author);
            log.info("Автор успешно обновлен: {} (id={})", savedAuthor.getFullName(), savedAuthor.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Автор успешно обновлен");
            return "redirect:/authors";

        } catch (RuntimeException e) {
            // DataIntegrityViolationException уже обрабатывается общим блоком Exception
            log.error("Автор с id={} не найден для обновления", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Автор не найден");
            return "redirect:/authors";

        } catch (Exception e) {
            log.error("Неизвестная ошибка при обновлении автора", e);
            model.addAttribute("error", "Ошибка при обновлении автора: " + e.getMessage());
            return "authors/form";
        }
    }

    // 5. УДАЛЕНИЕ АВТОРА
    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {

        log.debug("Удаление автора с id={}", id);

        try {
            // Проверяем, есть ли у автора книги
            Author author = authorService.getAuthorById(id);

            if (author.getBooks() != null && !author.getBooks().isEmpty()) {
                log.warn("Нельзя удалить автора {} (id={}), у которого есть книги",
                        author.getFullName(), id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Нельзя удалить автора, у которого есть книги. Сначала удалите или переназначьте книги.");
                return "redirect:/authors";
            }

            authorService.deleteAuthor(id);
            log.info("Автор успешно удален: {} (id={})", author.getFullName(), id);

            redirectAttributes.addFlashAttribute("successMessage", "Автор успешно удален");
            return "redirect:/authors";

        } catch (RuntimeException e) {
            log.error("Автор с id={} не найден для удаления", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Автор не найден");
            return "redirect:/authors";

        } catch (Exception e) {
            log.error("Неизвестная ошибка при удалении автора с id={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении автора: " + e.getMessage());
            return "redirect:/authors";
        }
    }

    // 6. ПРОСМОТР АВТОРА
    @GetMapping("/view/{id}")
    public String viewAuthor(@PathVariable Long id, Model model) {
        try {
            log.debug("Просмотр автора с id={}", id);
            Author author = authorService.getAuthorById(id);
            model.addAttribute("author", author);

        } catch (RuntimeException e) {
            log.error("Автор с id={} не найден", id, e);
            model.addAttribute("error", "Автор не найден");
            return "redirect:/authors";
        } catch (Exception e) {
            log.error("Ошибка при просмотре автора с id={}", id, e);
            model.addAttribute("error", "Ошибка при загрузке автора");
            return "redirect:/authors";
        }
        return "authors/view";
    }
}