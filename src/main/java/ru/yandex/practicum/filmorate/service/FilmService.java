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
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
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
    private final GenreDao genreDao;
    private final EventDao eventDao;

    @Autowired
    public FilmService(@Qualifier("filmBdStorage") FilmStorage filmStorage, UserStorage userStorage, LikeDao likeDao,
                       DirectorDao directorDao, GenreDao genreDao, EventDao eventDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDao = likeDao;
        this.directorDao = directorDao;
        this.genreDao = genreDao;
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

    public void delete(Long id) {
        filmStorage.delete(id);
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
            eventDao.addEvent(new Event(userId, id, "LIKE", "REMOVE"));
            log.info("Удален лайк: фильм {}, пользователь id={}", film.getName(), userId);
        } else {
            throw new NotFoundException("Лайк от указанного пользователя не найден");
        }
    }

    public List<Film> getPopularFilm(int count, Integer genreId, Integer year) {
        // проверим валидность присланного жанра
        if (genreId != null) {
            genreDao.findById(genreId);
        } else
            throw new NotFoundException("Попробуйте задать другой жанр для фильтрации популярных фильмов");
        // проверим валидность присланного года
        if (year != null && year < 0) {
            throw new NotFoundException("Год не может быть отрицательным");
        }
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
