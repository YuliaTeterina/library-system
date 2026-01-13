package com.example.library.controller;

import com.example.library.entity.Genre;
import com.example.library.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public String listGenres(
            @RequestParam(required = false) String search,
            Model model) {
        try {
            List<Genre> genres;
            if (search != null && !search.trim().isEmpty()) {
                genres = genreService.searchGenres(search);
                model.addAttribute("search", search);
            } else {
                genres = genreService.getAllGenres();
            }
            model.addAttribute("genres", genres);
            model.addAttribute("genreCount", genres.size());
        } catch (Exception e) {
            log.error("Ошибка при получении списка жанров", e);
            model.addAttribute("error", "Ошибка при загрузке списка жанров: " + e.getMessage());
        }
        return "genres/list";
    }

    @GetMapping("/form")
    public String showGenreForm(@RequestParam(required = false) Long id, Model model) {
        try {
            if (id != null && id > 0) {
                Genre genre = genreService.getGenreById(id);
                model.addAttribute("genre", genre);
            } else {
                model.addAttribute("genre", new Genre());
            }
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы жанра", e);
            model.addAttribute("error", "Ошибка при загрузке формы: " + e.getMessage());
            return "redirect:/genres";
        }
        return "genres/form";
    }

    @PostMapping("/save")
    public String saveGenre(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            Model model) {

        try {
            Genre genre;
            if (id != null && id > 0) {
                genre = genreService.getGenreById(id);
            } else {
                genre = new Genre();
            }

            genre.setName(name.trim());
            genre.setDescription(description != null ? description.trim() : null);

            genreService.saveGenre(genre);
            return "redirect:/genres?success";

        } catch (Exception e) {
            log.error("Ошибка при сохранении жанра", e);
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("description", description);
            return "genres/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Long id) {
        try {
            genreService.deleteGenre(id);
            return "redirect:/genres?deleted";
        } catch (Exception e) {
            log.error("Ошибка при удалении жанра с id={}", id, e);
            return "redirect:/genres?error=Ошибка при удалении жанра";
        }
    }

    @GetMapping("/view/{id}")
    public String viewGenre(@PathVariable Long id, Model model) {
        try {
            Genre genre = genreService.getGenreById(id);
            model.addAttribute("genre", genre);
        } catch (Exception e) {
            log.error("Ошибка при просмотре жанра с id={}", id, e);
            model.addAttribute("error", "Жанр не найден: " + e.getMessage());
            return "redirect:/genres";
        }
        return "genres/view";
    }
}