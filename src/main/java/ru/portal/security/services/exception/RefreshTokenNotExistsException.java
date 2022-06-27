package ru.portal.security.services.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenNotExistsException extends RuntimeException {

    static String MESSAGE = "Такого токена обновления не существует!";

    public RefreshTokenNotExistsException() {
        super(MESSAGE);
    }

    public RefreshTokenNotExistsException(String message) {
        super(message);
    }

    public RefreshTokenNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenNotExistsException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
