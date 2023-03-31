package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreDao {
    List<Genre> findAll();
    Genre findById(Integer genreId);
    Set<Genre> getFilmGenres(Long filmId);
    void setFilmGenre(Long filmId, Integer genre_id);
    void deleteFilmGenres(Long filmId);
}
