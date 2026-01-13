package com.example.library.controller;

import com.example.library.entity.Author;
import com.example.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public String listAuthors(
            @RequestParam(required = false) String search,
            Model model) {
        try {
            List<Author> authors;
            if (search != null && !search.trim().isEmpty()) {
                authors = authorService.searchAuthors(search);
                model.addAttribute("search", search);
            } else {
                authors = authorService.getAllAuthors();
            }
            model.addAttribute("authors", authors);
            model.addAttribute("authorCount", authors.size());
        } catch (Exception e) {
            log.error("Ошибка при получении списка авторов", e);
            model.addAttribute("error", "Ошибка при загрузке списка авторов: " + e.getMessage());
        }
        return "authors/list";
    }

    @GetMapping("/form")
    public String showAuthorForm(@RequestParam(required = false) Long id, Model model) {
        try {
            if (id != null && id > 0) {
                Author author = authorService.getAuthorById(id);
                model.addAttribute("author", author);
            } else {
                model.addAttribute("author", new Author());
            }
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы автора", e);
            model.addAttribute("error", "Ошибка при загрузке формы: " + e.getMessage());
            return "redirect:/authors";
        }
        return "authors/form";
    }

    @PostMapping("/save")
    public String saveAuthor(
            @RequestParam(required = false) Long id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) Integer birthYear,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String biography,
            Model model) {

        try {
            Author author;
            if (id != null && id > 0) {
                author = authorService.getAuthorById(id);
            } else {
                author = new Author();
            }

            author.setFirstName(firstName.trim());
            author.setLastName(lastName.trim());
            author.setBirthYear(birthYear);
            author.setCountry(country != null ? country.trim() : null);
            author.setBiography(biography != null ? biography.trim() : null);

            authorService.saveAuthor(author);
            return "redirect:/authors?success";

        } catch (Exception e) {
            log.error("Ошибка при сохранении автора", e);
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("birthYear", birthYear);
            model.addAttribute("country", country);
            model.addAttribute("biography", biography);
            return "authors/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        try {
            authorService.deleteAuthor(id);
            return "redirect:/authors?deleted";
        } catch (Exception e) {
            log.error("Ошибка при удалении автора с id={}", id, e);
            return "redirect:/authors?error=Ошибка при удалении автора";
        }
    }

    @GetMapping("/view/{id}")
    public String viewAuthor(@PathVariable Long id, Model model) {
        try {
            Author author = authorService.getAuthorById(id);
            model.addAttribute("author", author);
        } catch (Exception e) {
            log.error("Ошибка при просмотре автора с id={}", id, e);
            model.addAttribute("error", "Автор не найден: " + e.getMessage());
            return "redirect:/authors";
        }
        return "authors/view";
    }
}