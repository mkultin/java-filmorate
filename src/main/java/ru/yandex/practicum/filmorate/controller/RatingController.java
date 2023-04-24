package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public List<Mpa> getRatings() {
        log.debug("GET /mpa : get list of all Ratings");
        return ratingService.getRatings();
    }

    @GetMapping("/{id}")
    public Mpa getRatingById(@PathVariable Integer id) {
        log.debug("GET /mpa/{} : get Rating by ID", id);
        return ratingService.getRatingById(id);
    }

}
