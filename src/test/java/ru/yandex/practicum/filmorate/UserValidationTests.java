package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTests {
    private UserController userController;
    private User user;
    private static final Validator validator;
    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    public void beforeEach() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        user = userController.create(User.builder()
                .email("email@dom.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());
    }

    @Test
    public void shouldValidateEmail() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
        user.setEmail("email");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldValidateLogin() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
        user.setLogin("");
        violations = validator.validate(user);
        assertEquals(2, violations.size());
        user.setLogin("new login");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        user.setLogin(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldValidateName() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
        user.setName("");
        User newUser = userController.create(user);
        assertEquals(user.getLogin(), newUser.getName());
    }

    @Test
    public void shouldValidateBirthday() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
        user.setBirthday(LocalDate.of(2024, 1, 1));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }
}
