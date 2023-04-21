package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    Review addReview(@RequestBody @Valid Review review) {
        log.info("POST /reviews : create review - {}", review);
        return reviewService.create(review);
    }

    @PutMapping
    Review updateReview(@RequestBody @Valid Review review) {
        log.info("PUT /reviews/ : update review - {}", review);
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    void deleteReview(@PathVariable Long id) {
        log.info("DELETE /reviews/{} : delete review by ID", id);
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    Review getReviewById(@PathVariable Long id) {
        log.info("GET /reviews/{} : get review by ID", id);
        return reviewService.findById(id);
    }

    @GetMapping
    List<Review> getReviews(@RequestParam(value = "filmId", defaultValue = "0") Long filmId,
                            @RequestParam(value = "count", defaultValue = "10") int count) {
        log.info("GET /reviews?filmId={}&count={} : get list of reviews", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    void addLikeReview(@PathVariable Long id,
                       @PathVariable Long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    void addDislikeReview(@PathVariable Long id,
                          @PathVariable Long userId) {
        log.info("PUT /reviews/{}/dislike/{} : add dislike to review from user", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    void deleteLikeReview(@PathVariable Long id,
                          @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/like/{} : remove like for review from user", id, userId);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    void deleteDislikeReview(@PathVariable Long id,
                             @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/dislike/{} : remove dislike for review from user", id, userId);
        reviewService.deleteLike(id, userId);
    }
}
