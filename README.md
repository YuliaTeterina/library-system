# Итоговая работа по дисциплине "Язык программирования Java"
## Тетерина Юлия 7 группа 

<img width="1636" height="591" alt="image" src="https://github.com/user-attachments/assets/d0cea925-5974-45c8-9788-672404720984" />
<img width="1632" height="580" alt="image" src="https://github.com/user-attachments/assets/256457f3-3572-4da8-84f8-d9ef500e4634" />
<img width="1625" height="582" alt="image" src="https://github.com/user-attachments/assets/8a5c2346-970c-4523-b8d0-a04e03b6a433" />

Проект представляет из себя Spring Boot Web MVC приложение для управления библиотекой книг с тремя таблицами БД.

## Функциональность
* Просмотр списка книг/авторов/жанров
* Добавление новых книг/авторов/жанров
* Редактирование существующих книг/авторов/жанров
* Удаление книг/авторов/жанров
* Валидация вводимых данных

## Структура проекта
```
src/  
├── main/  
│   ├── java/com/example/library/  
│   │   ├── LibraryApplication.java              # главный класс приложения  
│   │   ├── controller/  
│   │   │   ├── BookController.java              # контроллер для операций с книгами  
│   │   │   ├── AuthorController.java            # контроллер для операций с авторами  
│   │   │   └── GenreController.java             # контроллер для операций с жанрами  
│   │   ├── entity/  
│   │   │   ├── Book.java                        # сущность книги  
│   │   │   ├── Author.java                      # сущность автора  
│   │   │   └── Genre.java                       # сущность жанра  
│   │   ├── repository/  
│   │   │   ├── BookRepository.java              # репозиторий для работы с таблицей книг  
│   │   │   ├── AuthorRepository.java            # репозиторий для работы с таблицей авторов  
│   │   │   └── GenreRepository.java             # репозиторий для работы с таблицей жанров  
│   │   └── service/  
│   │       ├── BookService.java                 # сервис с бизнес-логикой для книг  
│   │       ├── AuthorService.java               # сервис с бизнес-логикой для авторов  
│   │       └── GenreService.java                # сервис с бизнес-логикой для жанров  
│   └── resources/  
│       ├── static/css/  
│       │   └── style.css                        # CSS стили приложения  
│       ├── templates/  
│       │   ├── books/  
│       │   │   ├── list.html                    # страница со списком всех книг  
│       │   │   ├── form.html                    # форма добавления/редактирования книги  
│       │   │   └── view.html                    # страница просмотра деталей книги  
│       │   ├── authors/  
│       │   │   ├── list.html                    # страница со списком всех авторов  
│       │   │   ├── form.html                    # форма добавления/редактирования автора  
│       │   │   └── view.html                    # страница просмотра деталей автора  
│       │   └── genres/  
│       │       ├── list.html                    # страница со списком всех жанров  
│       │       ├── form.html                    # форма добавления/редактирования жанра  
│       │       └── view.html                    # страница просмотра деталей жанра  
│       ├── data.sql                             # SQL скрипт инициализации базы данных  
│       └── application.properties               # конфигурация приложения  
├── WEB-INF/                                     # папка для web-ресурсов  
│   └── docs/  
│       └── documentation.xml                    # XML документация проекта
```
