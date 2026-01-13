package com.example.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название книги обязательно")
    @Size(max = 200, message = "Название не должно превышать 200 символов")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull(message = "Автор обязателен")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    @NotNull(message = "Жанр обязателен")
    private Genre genre;

    @NotNull(message = "Год публикации обязателен")
    @Min(value = 1000, message = "Год должен быть не ранее 1000")
    @Max(value = 2100, message = "Год должен быть не позднее 2100")
    private Integer publicationYear;

    @Size(max = 20, message = "ISBN не должен превышать 20 символов")
    private String isbn;

    @Min(value = 1, message = "Количество страниц должно быть не менее 1")
    private Integer pages;

    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer quantity = 0;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;
}