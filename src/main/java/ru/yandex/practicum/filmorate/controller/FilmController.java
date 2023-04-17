package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@Positive @RequestParam(defaultValue = "10") int count,
                                     @RequestParam(required = false) Integer genreId,
                                     @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilm(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable Integer directorId, @RequestParam String sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
