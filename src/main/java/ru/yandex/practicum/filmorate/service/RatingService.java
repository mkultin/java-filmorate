package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDao;

import java.util.List;

@Service
public class RatingService {

    private final MpaDao mpaDao;

    public RatingService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public List<Mpa> getRatings() {
        return mpaDao.findAll();
    }

    public Mpa getRatingById(Integer id) {
        return mpaDao.findById(id);
    }
}
