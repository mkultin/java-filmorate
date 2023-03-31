package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmValidationTests {

    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;
    private final FilmService filmService;

    @BeforeEach
    public void beforeEach() {
        firstFilm = Film.builder()
                .name("The First Film")
                .description("Description of The First Film")
                .releaseDate(LocalDate.of(2000, 2, 4))
                .duration(120)
                .mpa(new Mpa(1, "Комедия"))
                .build();
        firstFilm.getGenres().add(new Genre(1, "G"));

        secondFilm = Film.builder()
                .name("The Second Film")
                .description("Description of The Second Film")
                .releaseDate(LocalDate.of(2004, 4, 8))
                .duration(140)
                .mpa(new Mpa(2, "Драма"))
                .build();
        secondFilm.getGenres().add(new Genre(2, "PG"));

        thirdFilm = Film.builder()
                .name("The Third Film")
                .description("Description of The Third Film")
                .releaseDate(LocalDate.of(2008, 6, 12))
                .duration(160)
                .mpa(new Mpa(3, "Мультфильм"))
                .build();
        thirdFilm.getGenres().add(new Genre(3, "PG-13"));
    }

    @Test
    void shouldCreateAndGetFilms() {
        filmService.addFilm(firstFilm);
        Film film = filmService.getFilmById(1L);
        assertEquals(1, film.getId());

        List<Film> films = filmService.getFilms();
        assertEquals(1, films.size());

        filmService.addFilm(secondFilm);
        film = filmService.getFilmById(2L);
        assertEquals(2, film.getId());

        films = filmService.getFilms();
        assertEquals(2, films.size());
    }

    @Test
    void updateFilm() {
        filmService.addFilm(firstFilm);
        Film updateFilm = Film.builder()
                .id(1L)
                .name("The First Film")
                .description("Description of The First Film")
                .releaseDate(LocalDate.of(2000, 2, 4))
                .duration(122)
                .mpa(new Mpa(1, "Комедия"))
                .build();
        updateFilm.getGenres().add(new Genre(1, "G"));
        updateFilm.getGenres().add(new Genre(2, "PG"));

        Film updatedFilm = filmService.updateFilm(updateFilm);
        Film film = filmService.getFilmById(1L);

        assertEquals(film.getDuration(), updatedFilm.getDuration());
        assertEquals(film.getGenres().size(), updatedFilm.getGenres().size());
    }
}
