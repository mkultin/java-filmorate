package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTests {
    private FilmController filmController;
    private Film film;
    private static final Validator validator;
    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    void beforeEach() throws ValidationException {
        filmController = new FilmController();
        film = filmController.create(Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build());
    }

    @Test
    public void shouldValidateName() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
        film.setName(" ");
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        film.setName(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldValidateDescription() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
        film.setDescription("В недалеком будущем мир населен людьми и репликантами, созданными выполнять самую тяжелую " +
                "работу. Работа офицера полиции Кей — держать репликантов под контролем в условиях нарастающего " +
                "напряжения. Он случайно становится обладателем секретной информации, которая ставит под угрозу " +
                "существование всего человечества. Желая найти ключ к разгадке, Кей решает разыскать Рика Декарда — " +
                "бывшего офицера специального подразделения полиции Лос-Анджелеса, который бесследно исчез много лет " +
                "назад.");
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldValidateReleaseDate() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
        film.setReleaseDate(LocalDate.of(1894, 1, 1));
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void shouldValidateDuration() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
        film.setDuration(0);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        film.setDuration(-100);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }
}
