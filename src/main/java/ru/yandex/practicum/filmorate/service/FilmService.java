package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.feed.EventDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeDao likeDao;
    private final DirectorDao directorDao;
    private final EventDao eventDao;

    @Autowired
    public FilmService(@Qualifier("filmBdStorage") FilmStorage filmStorage, LikeDao likeDao,
                       DirectorDao directorDao, EventDao eventDao) {
        this.filmStorage = filmStorage;
        this.likeDao = likeDao;
        this.directorDao = directorDao;
        this.eventDao = eventDao;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        Film newFilm = filmStorage.create(film);
        setFilmDirector(newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        Film newFilm = filmStorage.updateFilm(film);
        setFilmDirector(newFilm);
        return newFilm;
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);

        likeDao.addLike(id, userId);
        eventDao.addEvent(new Event(userId, id, "LIKE", "ADD"));
        log.info("Добавлен лайк: фильм {}, пользователь id={}", film.getName(), userId);
    }

    public void deleteLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);

        if (film.getLikes().contains(userId)) {
            likeDao.deleteLike(id, userId);
            eventDao.addEvent(new Event(userId, id, "LIKE", "DELETE"));
            log.info("Удален лайк: фильм {}, пользователь id={}", film.getName(), userId);
        } else {
            throw new NotFoundException("Лайк от указанного пользователя не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getDirectorFilms(Integer directorId, String sortBy) {
        directorDao.findById(directorId);
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    private void setFilmDirector(Film film) {
        Long filmId = film.getId();
        directorDao.updateFilmDirector(film);
        film.getDirectors().clear();
        film.getDirectors().addAll(directorDao.getFilmDirector(filmId));
    }
}
