package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorDao {

    List<Director> findAll();

    Director findById(Integer id);

    Director create(Director director);

    Director update(Director director);

    void deleteDirector(Integer id);

    Set<Director> getFilmDirector(Long filmId);

    void setFilmDirector(Long filmId, Integer directorId);

    void deleteFilmDirectors(Long filmId);
}
