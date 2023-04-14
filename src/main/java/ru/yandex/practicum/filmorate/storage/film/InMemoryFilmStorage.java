package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Фильм с id= " + id + "не найден.");
        }
    }

    @Override
    public Film create(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм {}, id={}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            films.put(film.getId(), film);
            log.info("Обновлен фильм {}, id={}", film.getName(), film.getId());
        } else {
            throw new NotFoundException("Фильм с id= " + film.getId() + "не найден.");
        }
        return film;
    }

    @Override
    public void delete(Long id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Удален фильм id={}", id);
        } else {
            throw new NotFoundException("Фильм с id= " + id + "не найден.");
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilms(Integer directorId, String sortBy) {
        return null;
    }

    @Override
    public List<Film> searchByTitle(String query) {
        return null;
    }

    @Override
    public List<Film> searchByDirector(String query) {
        return null;
    }

    @Override
    public List<Film> searchByTitleAndDirector(String query) {
        return null;
    }
}
