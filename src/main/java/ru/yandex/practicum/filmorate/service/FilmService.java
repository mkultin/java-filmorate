package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeDao likeDao;
    private final DirectorDao directorDao;

    @Autowired
    public FilmService(@Qualifier("filmBdStorage") FilmStorage filmStorage, LikeDao likeDao, DirectorDao directorDao) {
        this.filmStorage = filmStorage;
        this.likeDao = likeDao;
        this.directorDao = directorDao;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        if (film != null) {
            likeDao.addLike(id, userId);
            log.info("Добавлен лайк: фильм {}, пользователь id={}", film.getName(), userId);
        }
    }

    public void deleteLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        if (film != null) {
            if (film.getLikes().contains(userId)) {
                likeDao.deleteLike(id, userId);
                log.info("Удален лайк: фильм {}, пользователь id={}", film.getName(), userId);
            } else {
                throw new UserNotFoundException("Лайк от указанного пользователя не найден");
            }
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getDirectorFilms(Integer directorId, String sortBy) {
        Director director = directorDao.findById(directorId);
        if (director != null) {
            return filmStorage.getDirectorFilms(directorId, sortBy);
        } else {
            throw new FilmNotFoundException("Режиссер не найден.");
        }
    }
}
