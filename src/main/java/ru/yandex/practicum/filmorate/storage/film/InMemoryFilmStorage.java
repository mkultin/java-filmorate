package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
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
            throw new ValidationException("Фильм с указанным id не найден.");
        }
        return film;
    }

    @Override
    public void delete(Film film) {
        //films.remove(film);
        log.info("Удален фильм {}, id={}", film.getName(), film.getId());
    }
}
