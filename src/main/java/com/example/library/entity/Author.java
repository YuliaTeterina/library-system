package com.example.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
    private String lastName;

    @Min(value = 1000, message = "Год рождения должен быть не ранее 1000")
    @Max(value = 2100, message = "Год рождения должен быть не позднее 2100")
    private Integer birthYear;

    @Size(max = 100, message = "Страна не должна превышать 100 символов")
    private String country;

    @Size(max = 1000, message = "Биография не должна превышать 1000 символов")
    private String biography;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}