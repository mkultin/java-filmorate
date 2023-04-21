package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.feed.EventDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.rating.MpaDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDao likeDao;
    private final DirectorDao directorDao;
    private final EventDao eventDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    public List<Film> getFilms() {
        return filmStorage.getFilms().stream()
                .map(film -> makeFilm(film))
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        Film newFilm = filmStorage.create(film);
        film.setMpa(mpaDao.findById(film.getMpa().getId()));
        if (film.getGenres() != null) {
            setFilmGenres(film);
        }
        setFilmDirector(newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        Film newFilm = filmStorage.updateFilm(film);
        if (film.getGenres() != null) {
            setFilmGenres(film);
        } else {
            genreDao.deleteFilmGenres(film.getId());
        }
        setFilmDirector(newFilm);
        return newFilm;
    }

    public Film getFilmById(Long id) {
        return makeFilm(filmStorage.getFilmById(id));
    }

    public void delete(Long id) {
        filmStorage.delete(id);
    }

    public void addLike(Long id, Long userId) {
        Film film = getFilmById(id);
        if (film != null) {
            likeDao.addLike(id, userId);
            eventDao.addEvent(new Event(userId, id, EventType.LIKE, Operation.ADD));
            log.info("Добавлен лайк: фильм {}, пользователь id={}", film.getName(), userId);
        }
    }

    public void deleteLike(Long id, Long userId) {
        Film film = getFilmById(id);
        if (film != null) {
            if (film.getLikes().contains(userId)) {
                likeDao.deleteLike(id, userId);
                eventDao.addEvent(new Event(userId, id, EventType.LIKE, Operation.REMOVE));
                log.info("Удален лайк: фильм {}, пользователь id={}", film.getName(), userId);
            } else {
                throw new NotFoundException("Лайк от указанного пользователя не найден");
            }
        }
    }

    public List<Film> getPopularFilm(int count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilm(count, genreId, year).stream()
                .map(film -> makeFilm(film))
                .collect(Collectors.toList());
    }

    public List<Film> getDirectorFilms(Integer directorId, String sortBy) {
        directorDao.findById(directorId);
        return filmStorage.getDirectorFilms(directorId, sortBy).stream()
                .map(film -> makeFilm(film))
                .collect(Collectors.toList());
    }

    private void setFilmDirector(Film film) {
        Long filmId = film.getId();
        directorDao.deleteFilmDirectors(filmId);
        if (film.getDirectors() == null) {
            return;
        }
        directorDao.updateFilmDirector(film);
        film.getDirectors().clear();
        film.getDirectors().addAll(directorDao.getFilmDirector(filmId));
    }

    public List<Film> getCommonFilms(Long idUser, Long idFriend) {
        userStorage.getUserById(idUser);
        userStorage.getUserById(idFriend);
        return filmStorage.getCommonFilms(idUser, idFriend).stream()
                .map(film -> makeFilm(film))
                .collect(Collectors.toList());
    }

    public List<Film> search(String query, String groupBy) {
        switch (groupBy) {
            case "title":
                return filmStorage.searchByTitle(query).stream()
                    .map(film -> makeFilm(film))
                    .collect(Collectors.toList());
            case "director":
                return filmStorage.searchByDirector(query).stream()
                        .map(film -> makeFilm(film))
                        .collect(Collectors.toList());
            case "director,title":
            case "title,director":
                return filmStorage.searchByTitleAndDirector(query).stream()
                    .map(film -> makeFilm(film))
                    .collect(Collectors.toList());
            default:
                throw new BadRequestException("Incorrect parameters value");
        }
    }

    private Film makeFilm(Film film) {
        Long id = film.getId();
        film.getLikes().addAll(likeDao.getFilmLikes(id));
        film.getGenres().addAll(genreDao.getFilmGenres(id));
        film.getDirectors().addAll(directorDao.getFilmDirector(id));
        return film;
    }

    private void setFilmGenres(Film film) {
        Long filmId = film.getId();
        genreDao.deleteFilmGenres(filmId);
        for (Genre genre : film.getGenres()) {
            genre.setName(genreDao.findById(genre.getId()).getName());
            genreDao.setFilmGenre(filmId, genre.getId());
        }
        Set<Genre> genres = film.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        film.getGenres().clear();
        film.getGenres().addAll(genres);
    }
}
