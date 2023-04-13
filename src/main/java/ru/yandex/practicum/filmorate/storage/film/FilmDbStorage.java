package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.rating.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("filmBdStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final LikeDao likeDao;
    private final DirectorDao directorDao;

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM film";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlQuery = "SELECT * FROM film WHERE film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
            if (film == null) throw new NotFoundException("Фильм не найден");
            return film;
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        Long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(filmId);
        film.setMpa(mpaDao.findById(film.getMpa().getId()));
        if (film.getGenres() != null) {
            setFilmGenres(film);
        }
        log.info("Добавлен новый фильм {}, id={}", film.getName(), filmId);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Передан пустой film");
        }
        String sqlQuery = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "WHERE film_id = ?";
        int queryResult = jdbcTemplate.update(sqlQuery, film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (queryResult == 0) {
            throw new NotFoundException("Фильм не найден");
        }
        if (film.getGenres() != null) {
            setFilmGenres(film);
        } else {
            genreDao.deleteFilmGenres(film.getId());
        }
        log.info("Фильм {}, id={} обновлен!", film.getName(), film.getId());
        return film;
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new ValidationException("Передан пустой id");
        }
        String sqlQueryDelete = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryDelete, id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT * " +
                "FROM film AS f " +
                "LEFT JOIN (SELECT film_id, COUNT (user_id) AS rate " +
                "FROM FILM_LIKE " +
                "GROUP BY film_id) AS fl ON f.film_id = fl.film_id " +
                "ORDER BY rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, count);
    }

    @Override
    public List<Film> getDirectorFilms(Integer directorId, String sortBy) {
        String sqlQuery;
        if (sortBy.equals("likes")) {
            sqlQuery = "SELECT * " +
                    "FROM film AS f " +
                    "LEFT JOIN (SELECT film_id, COUNT (user_id) AS rate " +
                    "FROM FILM_LIKE " +
                    "GROUP BY film_id) AS fl ON f.film_id = fl.film_id " +
                    "WHERE f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?) " +
                    "ORDER BY rate DESC";
        } else {
            if (sortBy.equals("year")) {
                sqlQuery = "SELECT * " +
                        "FROM film AS f " +
                        "WHERE f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?) " +
                        "ORDER BY EXTRACT(YEAR FROM release_date)";
            } else {
                throw new ValidationException("Некорректный параметр сортирровки");
            }
        }
        return jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaDao.findById(resultSet.getInt("rating_id")))
                .build();
        film.getLikes().addAll(likeDao.getFilmLikes(film.getId()));
        film.getGenres().addAll(genreDao.getFilmGenres(film.getId()));
        film.getDirectors().addAll(directorDao.getFilmDirector(film.getId()));
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
