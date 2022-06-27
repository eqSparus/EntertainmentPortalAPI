package ru.portal.security.services.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IncorrectCredentialsException extends RuntimeException {

    static String MESSAGE = "Неправильное имя или пароль!";

    public IncorrectCredentialsException() {
        super(MESSAGE);
    }

    public IncorrectCredentialsException(String message) {
        super(message);
    }

    public IncorrectCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectCredentialsException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
