package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDao likeDao;

    @Autowired
    public FilmService(@Qualifier("filmBdStorage") FilmStorage filmStorage, UserStorage userStorage, LikeDao likeDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDao = likeDao;
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

    public List<Film> getCommonFilms(Long idUser, Long idFriend) {
        userStorage.getUserById(idUser);
        userStorage.getUserById(idFriend);
        List<Film> films = filmStorage.getCommonFilms(idUser, idFriend);
        return films;
    }
}
