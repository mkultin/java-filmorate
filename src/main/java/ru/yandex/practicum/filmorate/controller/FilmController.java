package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        log.debug("GET /films : get list of all films");
        return filmService.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("POST /films : create film - {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("PUT /films : update film - {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.debug("GET /films/{} : get film by ID", id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.debug("DELETE /films/{} : delete film by ID", id);
        filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("PUT /films/{}/like/{} : add like for film from user", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("DELETE /films/{}/like/{} : delete like for film from user", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@Positive @RequestParam(defaultValue = "10") int count,
                                     @RequestParam(required = false) Integer genreId,
                                     @RequestParam(required = false) Integer year) {
        log.info("getPopularFilm (GET /films/popular?count={}&genreId={}&year={}): Получить список из первых {} " +
                "фильмов по количеству лайков c фильтрацией по жанру (если 0, то без фильтрации по жанру) {} " +
                "и по году (если 0, то без фильтрации по жанру) {}", count, genreId, year, count, genreId, year);
        List<Film> films = filmService.getPopularFilm(count, genreId, year);
        log.info("getPopularFilm (GET /films/popular): Результат = {}", films);
        return films;
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable Integer directorId, @RequestParam String sortBy) {
        log.info("getDirectorFilms (GET /director?directorId={}&sortBy={}):  ", directorId, sortBy);
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("getCommonFilms (GET /common?userId={}&friendId={}):", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.info("searchFilms (GET /films/search?query={}&by={}):  ", query, by);
        List<Film> films = filmService.search(query, by);
        log.info("searchFilms (GET /films/search?query={}&by={}): Результат = {}", query, by, films);
        return films;
    }
}
