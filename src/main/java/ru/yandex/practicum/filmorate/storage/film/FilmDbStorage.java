package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component
@Qualifier("filmBdStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlQuery = "SELECT * " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id = ?";
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
        validateQueryResult(queryResult);
        log.info("Фильм {}, id={} обновлен!", film.getName(), film.getId());
        return film;
    }

    @Override
    public void delete(Long id) {
        String sqlQueryDelete = "DELETE FROM film WHERE film_id = ?";
        int queryResult = jdbcTemplate.update(sqlQueryDelete, id);
        validateQueryResult(queryResult);
        log.info("Фильм id = {} удален", id);
    }

    @Override
    public List<Film> getPopularFilm(int count, Integer genreId, Integer year) {
        StringBuilder getPopularFilmsSql = new StringBuilder();
        getPopularFilmsSql.append(
                "SELECT * " +
                        "FROM FILM AS f " +
                        "JOIN RATING AS r ON (r.rating_id = f.rating_id) " +
                        "LEFT JOIN " +
                        "(SELECT film_id, COUNT(user_id) as rate " +
                        "FROM FILM_LIKE " +
                        "GROUP BY film_id) fl ON (fl.film_id = f.film_id) ");
        if (genreId != null) {
            getPopularFilmsSql.append(
                    "JOIN FILM_GENRE g ON (g.film_id = f.film_id AND g.genre_id = ").append(genreId).append(") ");
        }
        if (year != null) {
            getPopularFilmsSql.append(
                    "WHERE EXTRACT(YEAR from CAST(f.release_date AS DATE)) = ").append(year).append(" ");
        }
        getPopularFilmsSql.append(
                "ORDER BY fl.rate DESC " +
                        "LIMIT ?");
        return jdbcTemplate.query(getPopularFilmsSql.toString(), this::makeFilm, count);
    }

    @Override
    public List<Film> getDirectorFilms(Integer directorId, String sortBy) {
        String sqlQuery;
        if (sortBy.equals("likes")) {
            sqlQuery = "SELECT * " +
                    "FROM film AS f " +
                    "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN (SELECT film_id, COUNT (user_id) AS rate " +
                    "FROM FILM_LIKE " +
                    "GROUP BY film_id) AS fl ON f.film_id = fl.film_id " +
                    "WHERE f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?) " +
                    "ORDER BY rate DESC";
        } else {
            if (sortBy.equals("year")) {
                sqlQuery = "SELECT * " +
                        "FROM film AS f " +
                        "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                        "WHERE f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?) " +
                        "ORDER BY EXTRACT(YEAR FROM release_date)";
            } else {
                throw new ValidationException("Некорректный параметр сортирровки");
            }
        }
        return jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
    }

    @Override
    public List<Film> getCommonFilms(Long idUser, Long idFriend) {
        final String searchFilmsSql = "SELECT * " +
                "FROM FILM f " +
                "JOIN RATING R on R.RATING_ID = f.RATING_ID " +
                "JOIN FILM_LIKE l1 on (l1.film_id= f.film_id AND l1.user_id = ?) " +
                "JOIN FILM_LIKE l2 on (l2.film_id= f.film_id AND l2.user_id = ?) " +
                "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) as rate " +
                "FROM FILM_LIKE " +
                "GROUP BY film_id) fl ON (fl.film_id = f.film_id) " +
                "ORDER BY fl.rate DESC ";
        return jdbcTemplate.query(searchFilmsSql, this::makeFilm, idUser, idFriend);
    }

    @Override
    public List<Film> searchByTitle(String query) {
        String sql = "SELECT * " +
                "FROM FILM AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "WHERE LOCATE(?, LOWER(f.NAME)) > 0";
        return jdbcTemplate.query(sql, this::makeFilm, query.toLowerCase());
    }

    @Override
    public List<Film> searchByDirector(String query) {
        String sql = "select * from film as f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_director as fd ON fd.film_id = f.film_id " +
                "LEFT JOIN director AS d ON fd.director_id = d.director_id " +
                "where locate(?, lower(d.name)) > 0";
        return jdbcTemplate.query(sql, this::makeFilm, query.toLowerCase());
    }

    @Override
    public List<Film> searchByTitleAndDirector(String query) {
        String sql = "select * from film as f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_director as fd ON fd.film_id = f.film_id " +
                "LEFT JOIN director AS d ON fd.director_id = d.director_id " +
                "where (locate(?, lower(f.name)) > 0 or locate(?, lower(d.name)) > 0)";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, query.toLowerCase(), query.toLowerCase()).stream()
                .distinct()
                .collect(Collectors.toList());
        Collections.reverse(films);
        return films;
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new Mpa(
                        resultSet.getInt("film.rating_id"),
                        resultSet.getString("rating.name")
                ))
                .build();
        return film;
    }

    private void validateQueryResult(int queryResult) {
        if (queryResult == 0) {
            throw new NotFoundException("Фильм не найден");
        }
    }
}
