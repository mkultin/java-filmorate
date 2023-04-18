package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.rating.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikeDaoImpl implements LikeDao {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;

    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "MERGE INTO film_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        int queryResult = jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Set<Long> getFilmLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_like WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    @Override
    public Set<Film> getRecommendedFilms(Long userId) {
        String sqlQuery = "SELECT * " +
                "FROM film " +
                "WHERE film_id IN (" +
                "SELECT film_id " +
                "FROM film_like " +
                "WHERE user_id IN (" +
                    "SELECT user_id " +
                    "FROM film_like " +
                    "WHERE film_id IN (SELECT film_id FROM film_like WHERE user_id = ?) AND user_id <> ? " +
                    "GROUP BY user_id " +
                    "ORDER BY COUNT(film_id) DESC " +
                    "LIMIT 1) " +
                "AND film_id NOT IN (SELECT film_id FROM film_like WHERE user_id = ?))";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::makeFilm, userId, userId, userId));
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
        film.getLikes().addAll(getFilmLikes(film.getId()));
        film.getGenres().addAll(genreDao.getFilmGenres(film.getId()));
        film.getDirectors().addAll(directorDao.getFilmDirector(film.getId()));
        return film;
    }
}
