package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewService(ReviewDao reviewDao,
                         @Qualifier("filmBdStorage") FilmStorage filmStorage,
                         @Qualifier("userBdStorage") UserStorage userStorage) {
        this.reviewDao = reviewDao;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Review create(Review review) {
        validateReview(review);
        return reviewDao.create(review);
    }

    public Review update(Review review) {
        validateReview(review);
        return reviewDao.update(review);
    }

    public void delete(Long reviewId) {
        reviewDao.delete(reviewId);
    }

    public Review findById(Long reviewId) {
        return reviewDao.findById(reviewId);
    }

    public void addLike(Long reviewId, Long userId) {
        validateReviewById(reviewId);
        validateUser(userId);
        reviewDao.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        validateReviewById(reviewId);
        validateUser(userId);
        reviewDao.addDislike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        validateReviewById(reviewId);
        validateUser(userId);
        reviewDao.deleteLike(reviewId, userId);
    }

    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        validateFilm(filmId);
        return reviewDao.getReviewsByFilmId(filmId, count);
    }

    public List<Review> getReviews(int count) {
        return reviewDao.getReviews(count);
    }

    private void validateReview(Review review) {
        validateUser(review.getUserId());
        validateFilm(review.getFilmId());
    }

    private void validateReviewById(Long reviewId) {
        if (findById(reviewId) == null) {
            throw new NotFoundException("Отзыв с таким id не найден");
        }
    }

    private void validateUser(Long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с таким id не найден.");
        }
    }

    private void validateFilm(Long filmId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
    }
}
