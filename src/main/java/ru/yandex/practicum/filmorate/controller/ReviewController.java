package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    Review addReview(@RequestBody @Valid Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    Review updateReview(@RequestBody @Valid Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    void deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    Review getReviewById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @GetMapping
    List<Review> getReviews(@RequestParam(value = "filmId", defaultValue = "0") Long filmId,
                            @RequestParam(value = "count", defaultValue = "10") int count) {
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
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    void deleteLikeReview(@PathVariable Long id,
                       @PathVariable Long userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    void deleteDislikeReview(@PathVariable Long id,
                          @PathVariable Long userId) {
        reviewService.deleteLike(id, userId);
    }
}
