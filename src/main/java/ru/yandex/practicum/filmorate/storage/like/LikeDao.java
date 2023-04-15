package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface LikeDao {

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Set<Long> getFilmLikes(Long filmId);

    Set<Film> getRecommendedFilms(Long userId);
}
