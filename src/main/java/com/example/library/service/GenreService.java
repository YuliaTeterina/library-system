package com.example.library.service;

import com.example.library.entity.Genre;
import com.example.library.repository.GenreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Жанр не найден с id: " + id));
    }

    public Genre saveGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }

    public List<Genre> searchGenres(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllGenres();
        }
        String searchTerm = keyword.trim();
        return genreRepository.findByNameContainingIgnoreCase(searchTerm);
    }
}