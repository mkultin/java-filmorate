package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDaoImpl (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review")
                .usingGeneratedKeyColumns("review_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue();
        review.setReviewId(id);
        review.setUseful(0);
        log.info("Добавлен новый отзыв id = {}", review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE review SET content = ?, is_positive = ? " +
                "WHERE review_id = ?";
        int queryResponse = jdbcTemplate.update(sqlQuery, review.getContent(),
                review.getIsPositive(), review.getReviewId());
        validateQueryResult(queryResponse);
        log.info("Обновлен отзыв id = {}", review.getReviewId());
        return findById(review.getReviewId());
    }

    @Override
    public void delete(Long reviewId) {
        String sqlQuery = "DELETE FROM review WHERE review_id = ?";
        int queryResponse = jdbcTemplate.update(sqlQuery, reviewId);
        validateQueryResult(queryResponse);
        log.info("Удален отзыв id = {}", reviewId);
    }

    @Override
    public Review findById(Long reviewId) {
        String sqlQuery = "SELECT r.*, SUM(rate.total) AS useful " +
                "FROM review AS r " +
                "LEFT JOIN (SELECT COUNT(user_id) AS total, review_id " +
                    "FROM like_review " +
                    "WHERE is_like = TRUE " +
                    "GROUP BY review_id " +
                    "UNION ALL " +
                    "SELECT -1*COUNT(user_id) AS total, review_id " +
                    "FROM like_review " +
                    "WHERE is_like = FALSE " +
                    "GROUP BY review_id) AS rate ON r.review_id = rate.review_id " +
                "WHERE r.review_id = ? " +
                "GROUP BY r.review_id ";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        if (sqlRowSet.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeReview, reviewId);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        String sqlQuery = "INSERT INTO like_review (review_id, user_id, is_like)" +
                "VALUES (?, ?, TRUE)";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
        log.info("Отзыву id = {} добавлен лайк от пользователя id = {}.", reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        String sqlQuery = "INSERT INTO like_review (review_id, user_id, is_like)" +
                "VALUES (?, ?, FALSE)";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
        log.info("Отзыву id = {} добавлен дизлайк от пользователя id = {}.", reviewId, userId);

    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        String sqlQuery = "DELETE FROM like_review WHERE review_id = ? AND user_id = ?";
        int queryResult = jdbcTemplate.update(sqlQuery, reviewId, userId);
        validateQueryResult(queryResult);
        log.info("Для отзыва id = {} удален лайк от пользователя id = {} .", reviewId, userId);

    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        String sqlQuery = "SELECT r.*, " +
                "CASE WHEN SUM(rate.total) IS NULL THEN 0 ELSE SUM(rate.total) END AS useful " +
                "FROM review AS r " +
                "LEFT JOIN (SELECT COUNT(user_id) AS total, review_id " +
                    "FROM like_review " +
                    "WHERE is_like = TRUE " +
                    "GROUP BY review_id " +
                    "UNION ALL " +
                    "SELECT -1*COUNT(user_id) AS total, review_id " +
                    "FROM like_review " +
                    "WHERE is_like = FALSE " +
                    "GROUP BY review_id) AS rate ON r.review_id = rate.review_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeReview, filmId, count);
    }

    @Override
    public List<Review> getReviews(int count) {
        String sqlQuery = "SELECT r.*, " +
                "CASE WHEN SUM(rate.total) IS NULL THEN 0 ELSE SUM(rate.total) END AS useful " +
                "FROM review AS r " +
                "LEFT JOIN (SELECT COUNT(user_id) AS total, review_id " +
                    "FROM like_review " +
                    "WHERE is_like = TRUE " +
                    "GROUP BY review_id " +
                    "UNION ALL " +
                    "SELECT -1*COUNT(user_id) AS total, review_id " +
                    "FROM like_review " +
                    "WHERE is_like = FALSE " +
                    "GROUP BY review_id) AS rate ON r.review_id = rate.review_id " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeReview, count);
    }

    private Review makeReview(ResultSet resultSet, int rowNum) throws SQLException {
        return new Review(
                resultSet.getLong("review_id"),
                resultSet.getString("content"),
                resultSet.getBoolean("is_positive"),
                resultSet.getLong("user_id"),
                resultSet.getLong("film_id"),
                resultSet.getInt("useful")
        );
    }

    private void validateQueryResult(int queryResult) {
        if (queryResult == 0) {
            throw new NotFoundException("Отзыв не найден.");
        }
    }
}
