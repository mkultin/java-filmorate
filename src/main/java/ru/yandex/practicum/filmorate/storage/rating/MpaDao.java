package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {

    List<Mpa> findAll();

    Mpa findById(Integer id);
}
