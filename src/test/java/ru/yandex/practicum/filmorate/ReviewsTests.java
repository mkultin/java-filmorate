package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewsTests {

    private final ReviewService reviewService;
    private final FilmService filmService;
    private final UserService userService;
    private User user1;
    private User user2;
    private Film film1;
    private Film film2;
    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .email("first@user.ru")
                .login("first_user")
                .name("First Name")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();

        user2 = User.builder()
                .email("second@user.ru")
                .login("second_user")
                .name("Second Name")
                .birthday(LocalDate.of(1998, 8, 8))
                .build();

        film1 = Film.builder()
                .name("The First Film")
                .description("Description of The First Film")
                .releaseDate(LocalDate.of(2000, 2, 4))
                .duration(120)
                .mpa(new Mpa(1, "Комедия"))
                .build();
        film1.getGenres().add(new Genre(1, "G"));

        film2 = Film.builder()
                .name("The Second Film")
                .description("Description of The Second Film")
                .releaseDate(LocalDate.of(2004, 4, 8))
                .duration(140)
                .mpa(new Mpa(2, "Драма"))
                .build();
        film2.getGenres().add(new Genre(2, "PG"));

        review1 = new Review();
        review1.setContent("the bad film");
        review1.setIsPositive(false);

        review2 = new Review();
        review2.setContent("good film");
        review2.setIsPositive(true);

        review3 = new Review();
        review3.setContent("good film");
        review3.setIsPositive(true);
    }

    @Test
    void shouldCRUDReview() {
        user1 = userService.addUser(user1);
        film1 = filmService.addFilm(film1);
        user2 = userService.addUser(user2);
        film2 = filmService.addFilm(film2);

        review1.setFilmId(film1.getId());
        review1.setUserId(user1.getId());
        Review createdReview = reviewService.create(review1);

        assertThat(createdReview.getReviewId()).isEqualTo(1);
        assertThat(createdReview.getContent()).isEqualTo(review1.getContent());

        review1 = createdReview;

        Review review = new Review();
        review.setReviewId(review1.getReviewId());
        review.setContent("the good film");
        review.setIsPositive(true);
        review.setFilmId(2L);
        review.setUserId(2L);

        Review updatedReview = reviewService.update(review);

        assertThat(updatedReview.getReviewId()).isEqualTo(1);
        assertThat(updatedReview.getContent()).isEqualTo(review.getContent());
        assertThat(updatedReview.getUserId()).isEqualTo(user1.getId());
        assertThat(updatedReview.getFilmId()).isEqualTo(film1.getId());

        review1 = updatedReview;

        review2.setFilmId(film2.getId());
        review2.setUserId(user1.getId());
        review2 = reviewService.create(review2);
        review3.setFilmId(film1.getId());
        review3.setUserId(user2.getId());
        review3 = reviewService.create(review3);

        List<Review> reviewList = reviewService.getReviews(0L, 4);

        assertThat(reviewList.size()).isEqualTo(3);

        reviewService.addLike(review2.getReviewId(), user1.getId());
        review2 = reviewService.findById(review2.getReviewId());
        reviewService.addDislike(review1.getReviewId(), user2.getId());
        review1 = reviewService.findById(review1.getReviewId());

        reviewList = reviewService.getReviews(0L, 3);
        assertThat(reviewList.get(0)).isEqualTo(review2);
        assertThat(reviewList.get(1)).isEqualTo(review3);
        assertThat(reviewList.get(2)).isEqualTo(review1);

        reviewList = reviewService.getReviews(film1.getId(), 10);
        assertThat(reviewList.get(0)).isEqualTo(review3);
        assertThat(reviewList.size()).isEqualTo(2);

        //*
        reviewService.addDislike(review2.getReviewId(), user1.getId());
        review2 = reviewService.findById(review2.getReviewId());

        assertThat(review2.getUseful()).isEqualTo(-1);

        reviewService.delete(review1.getReviewId());

        reviewList = reviewService.getReviews(0L, 3);
        assertThat(reviewList.size()).isEqualTo(2);
    }
}
