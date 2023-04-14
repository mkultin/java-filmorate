package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceTests {

    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;
    private final FilmService filmService;
    private final UserService userService;
    private final DirectorDao directorDao;
    private Director director;

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

        director = new Director();
        director.setName("Director");
    }

    @Test
    void shouldGetFilmById() {
        filmService.addFilm(firstFilm);
        Film film = filmService.getFilmById(1L);
        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(1);

        filmService.addFilm(secondFilm);
        film = filmService.getFilmById(2L);
        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(2);
    }

    @Test
    void shouldGetFilms() {
        filmService.addFilm(firstFilm);
        List<Film> films = filmService.getFilms();
        assertThat(films).contains(firstFilm);

        filmService.addFilm(secondFilm);
        films = filmService.getFilms();
        assertThat(films).contains(secondFilm);
    }

    @Test
    void updateFilm() {
        filmService.addFilm(firstFilm);
        Film filmToUpdate = Film.builder()
                .id(1L)
                .name("The First Film")
                .description("Description of The First Film")
                .releaseDate(LocalDate.of(2000, 2, 4))
                .duration(122)
                .mpa(new Mpa(1, "Комедия"))
                .build();
        filmToUpdate.getGenres().add(new Genre(1, "G"));
        filmToUpdate.getGenres().add(new Genre(2, "PG"));
        director = directorDao.create(director);
        filmToUpdate.getDirectors().add(director);

        filmService.updateFilm(filmToUpdate);
        Film updatedFilm = filmService.getFilmById(1L);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getDuration()).isEqualTo(filmToUpdate.getDuration());
        assertThat(updatedFilm.getGenres().size()).isEqualTo(2);
        assertThat(updatedFilm.getDirectors().size()).isEqualTo(1);
    }

    @Test
    void shouldAddAndDeleteLike() {
        filmService.addFilm(firstFilm);
        filmService.addFilm(secondFilm);
        filmService.addFilm(thirdFilm);

        User firstUser = User.builder()
                .email("first@user.ru")
                .login("first_user")
                .name("First Name")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();

        User secondUser = User.builder()
                .email("second@user.ru")
                .login("second_user")
                .name("Second Name")
                .birthday(LocalDate.of(1998, 8, 8))
                .build();

        User thirdUser = User.builder()
                .email("third@user.ru")
                .login("third_user")
                .name("Third Name")
                .birthday(LocalDate.of(1997, 7, 7))
                .build();

        userService.addUser(firstUser);
        userService.addUser(secondUser);
        userService.addUser(thirdUser);

        filmService.addLike(firstFilm.getId(), firstUser.getId());
        filmService.addLike(firstFilm.getId(), secondUser.getId());
        filmService.addLike(firstFilm.getId(), thirdUser.getId());
        filmService.addLike(thirdFilm.getId(), firstUser.getId());
        filmService.addLike(thirdFilm.getId(), thirdUser.getId());
        filmService.addLike(secondFilm.getId(), secondUser.getId());

        List<Film> popularFilm = filmService.getPopularFilm(10, 1, 1);

        assertThat(popularFilm.size()).isEqualTo(3);
        assertThat(popularFilm.get(0)).isEqualTo(filmService.getFilmById(firstFilm.getId()));

        filmService.deleteLike(firstFilm.getId(), firstUser.getId());
        filmService.deleteLike(firstFilm.getId(), secondUser.getId());
        filmService.deleteLike(firstFilm.getId(), thirdUser.getId());

        popularFilm = filmService.getPopularFilm(10, 1, 1);

        assertThat(popularFilm.size()).isEqualTo(3);
        assertThat(popularFilm.get(0)).isEqualTo(filmService.getFilmById(thirdFilm.getId()));
    }

    @Test
    void shouldGetDirectorFilms() {
        director = directorDao.create(director);
        firstFilm.getDirectors().add(director);
        firstFilm = filmService.addFilm(firstFilm);
        secondFilm.getDirectors().add(director);
        secondFilm = filmService.addFilm(secondFilm);
        thirdFilm.getDirectors().add(director);
        thirdFilm = filmService.addFilm(thirdFilm);

        List<Film> directorFilms = filmService.getDirectorFilms(director.getId(), "likes");

        assertThat(directorFilms).isNotNull();
        assertThat(directorFilms.size()).isEqualTo(3);

        directorFilms = filmService.getDirectorFilms(director.getId(), "year");

        assertThat(directorFilms).isNotNull();
        assertThat(directorFilms.size()).isEqualTo(3);
        assertThat(directorFilms.get(2)).isEqualTo(thirdFilm);
    }
}
