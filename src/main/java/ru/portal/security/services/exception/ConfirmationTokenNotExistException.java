package ru.portal.security.services.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfirmationTokenNotExistException extends RuntimeException {

    static String MESSAGE = "Такого токена подтверждения не существует!";

    public ConfirmationTokenNotExistException() {
        super(MESSAGE);
    }

    public ConfirmationTokenNotExistException(String message) {
        super(message);
    }

    public ConfirmationTokenNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfirmationTokenNotExistException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
