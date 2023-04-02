package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDao;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    private final MpaDao mpaDao;

    @GetMapping
    public List<Mpa> getRatings() {
        return mpaDao.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getRatingById(@PathVariable Integer id) {
        return mpaDao.findById(id);
    }

}
