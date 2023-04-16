package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    Review create(Review review);

    Review update(Review review);

    void delete(Long reviewId);

    Review findById(Long reviewId);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    List<Review> getReviewsByFilmId(Long filmId, int count);

    List<Review> getReviews(int count);
}
