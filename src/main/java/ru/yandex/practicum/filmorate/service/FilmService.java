package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
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
    private final DirectorDao directorDao;


    @Autowired
    public FilmService(@Qualifier("filmBdStorage") FilmStorage filmStorage, UserStorage userStorage, LikeDao likeDao, DirectorDao directorDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDao = likeDao;
        this.directorDao = directorDao;
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

    public void delete(Long id) {
        filmStorage.delete(id);
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
                throw new NotFoundException("Лайк от указанного пользователя не найден");
            }
        }
    }

    public List<Film> getPopularFilm(int count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilm(count, genreId, year);
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

    public List<Film> getCommonFilms(Long idUser, Long idFriend) {
        userStorage.getUserById(idUser);
        userStorage.getUserById(idFriend);
        return filmStorage.getCommonFilms(idUser, idFriend);
    }
}
