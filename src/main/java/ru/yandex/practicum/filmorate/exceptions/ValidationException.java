package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends Exception {

    public ValidationException() {

    }

    public ValidationException(final String message) {
        super(message);
        log.warn(message);
    }
}
