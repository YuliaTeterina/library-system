package com.example.library.controller;

import com.example.library.entity.Genre;
import com.example.library.service.GenreService;
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
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    // 1. СПИСОК ВСЕХ ЖАНРОВ
    @GetMapping
    public String listGenres(Model model) {
        try {
            log.debug("Получение списка жанров");
            List<Genre> genres = genreService.getAllGenres();
            model.addAttribute("genres", genres);
            model.addAttribute("genreCount", genres.size());

            // Проверяем параметры URL для сообщений
            if (model.containsAttribute("successMessage")) {
                model.addAttribute("success", model.getAttribute("successMessage"));
            }
            if (model.containsAttribute("errorMessage")) {
                model.addAttribute("error", model.getAttribute("errorMessage"));
            }

        } catch (Exception e) {
            log.error("Ошибка при получении списка жанров", e);
            model.addAttribute("error", "Не удалось загрузить список жанров: " + e.getMessage());
        }
        return "genres/list";
    }

    // 2. ФОРМА ДОБАВЛЕНИЯ/РЕДАКТИРОВАНИЯ ЖАНРА
    @GetMapping("/form")
    public String showGenreForm(@RequestParam(required = false) Long id, Model model) {
        try {
            log.debug("Открытие формы жанра, id={}", id);

            if (id != null && id > 0) {
                // Редактирование существующего жанра
                Genre genre = genreService.getGenreById(id);
                model.addAttribute("genre", genre);
                log.debug("Редактирование жанра: {}", genre.getName());
            } else {
                // Создание нового жанра
                model.addAttribute("genre", new Genre());
                log.debug("Создание нового жанра");
            }

        } catch (RuntimeException e) {
            log.error("Жанр с id={} не найден", id, e);
            model.addAttribute("error", "Жанр не найден");
            return "redirect:/genres";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы жанра", e);
            model.addAttribute("error", "Ошибка при загрузке формы: " + e.getMessage());
            return "redirect:/genres";
        }
        return "genres/form";
    }

    // 3. СОЗДАНИЕ НОВОГО ЖАНРА
    @PostMapping
    public String createGenre(@Valid @ModelAttribute Genre genre,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        log.debug("Создание жанра: {}", genre.getName());

        // Валидация
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании жанра: {}", bindingResult.getAllErrors());
            return "genres/form";
        }

        try {
            // Проверка уникальности названия (опционально)
            List<Genre> existingGenres = genreService.getAllGenres();
            boolean nameExists = existingGenres.stream()
                    .anyMatch(g -> g.getName().equalsIgnoreCase(genre.getName()));

            if (nameExists) {
                model.addAttribute("error", "Жанр с таким названием уже существует");
                return "genres/form";
            }

            // Сохраняем
            Genre savedGenre = genreService.saveGenre(genre);
            log.info("Жанр успешно создан: {} (id={})", savedGenre.getName(), savedGenre.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Жанр успешно добавлен");
            return "redirect:/genres";

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности данных при создании жанра", e);
            model.addAttribute("error", "Ошибка: возможно, жанр с таким названием уже существует");
            return "genres/form";

        } catch (Exception e) {
            log.error("Неизвестная ошибка при создании жанра", e);
            model.addAttribute("error", "Ошибка при сохранении жанра: " + e.getMessage());
            return "genres/form";
        }
    }

    // 4. ОБНОВЛЕНИЕ СУЩЕСТВУЮЩЕГО ЖАНРА
    @PostMapping("/update/{id}")
    public String updateGenre(@PathVariable Long id,
                              @Valid @ModelAttribute Genre genre,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        log.debug("Обновление жанра с id={}", id);

        // Валидация
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении жанра: {}", bindingResult.getAllErrors());
            return "genres/form";
        }

        try {
            // Проверяем, существует ли жанр
            Genre existingGenre = genreService.getGenreById(id);

            // Проверка уникальности названия (кроме текущего жанра)
            List<Genre> allGenres = genreService.getAllGenres();
            boolean nameExists = allGenres.stream()
                    .filter(g -> !g.getId().equals(id))
                    .anyMatch(g -> g.getName().equalsIgnoreCase(genre.getName()));

            if (nameExists) {
                model.addAttribute("error", "Жанр с таким названием уже существует");
                model.addAttribute("genre", existingGenre);
                return "genres/form";
            }

            // Устанавливаем ID
            genre.setId(id);

            // Сохраняем
            Genre savedGenre = genreService.saveGenre(genre);
            log.info("Жанр успешно обновлен: {} (id={})", savedGenre.getName(), savedGenre.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Жанр успешно обновлен");
            return "redirect:/genres";

        } catch (RuntimeException e) {
            log.error("Жанр с id={} не найден для обновления", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Жанр не найден");
            return "redirect:/genres";

        } catch (Exception e) {
            log.error("Неизвестная ошибка при обновлении жанра", e);
            model.addAttribute("error", "Ошибка при обновлении жанра: " + e.getMessage());
            return "genres/form";
        }
    }

    // 5. УДАЛЕНИЕ ЖАНРА
    @GetMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {

        log.debug("Удаление жанра с id={}", id);

        try {
            // Проверяем, есть ли у жанра книги
            Genre genre = genreService.getGenreById(id);

            if (genre.getBooks() != null && !genre.getBooks().isEmpty()) {
                log.warn("Нельзя удалить жанр {} (id={}), у которого есть книги",
                        genre.getName(), id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Нельзя удалить жанр, у которого есть книги. Сначала удалите или переназначьте книги.");
                return "redirect:/genres";
            }

            genreService.deleteGenre(id);
            log.info("Жанр успешно удален: {} (id={})", genre.getName(), id);

            redirectAttributes.addFlashAttribute("successMessage", "Жанр успешно удален");
            return "redirect:/genres";

        } catch (RuntimeException e) {
            log.error("Жанр с id={} не найден для удаления", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Жанр не найден");
            return "redirect:/genres";

        } catch (Exception e) {
            log.error("Неизвестная ошибка при удалении жанра с id={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении жанра: " + e.getMessage());
            return "redirect:/genres";
        }
    }

    // 6. ПРОСМОТР ЖАНРА
    @GetMapping("/view/{id}")
    public String viewGenre(@PathVariable Long id, Model model) {
        try {
            log.debug("Просмотр жанра с id={}", id);
            Genre genre = genreService.getGenreById(id);
            model.addAttribute("genre", genre);

        } catch (RuntimeException e) {
            log.error("Жанр с id={} не найден", id, e);
            model.addAttribute("error", "Жанр не найден");
            return "redirect:/genres";
        } catch (Exception e) {
            log.error("Ошибка при просмотре жанра с id={}", id, e);
            model.addAttribute("error", "Ошибка при загрузке жанра");
            return "redirect:/genres";
        }
        return "genres/view";
    }
}