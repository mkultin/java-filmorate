package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film getFilmById(Long id);

    Film create(Film film);

    Film updateFilm(Film film);

    void delete(Long id);

    List<Film> getPopularFilm(int count, Integer genreId, Integer year);

    List<Film> getCommonFilms(Long idUser, Long idFriend);

    List<Film> getDirectorFilms(Integer directorId, String sortBy);
}
