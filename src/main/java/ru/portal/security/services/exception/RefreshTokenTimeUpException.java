package ru.portal.security.services.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Исключение бросаеться если время жизни токена обновления просрочено.<br>
 * Сообщение по умолчанию {@value MESSAGE}
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenTimeUpException extends RuntimeException {

    static String MESSAGE = "Время действия токена обновления истекло";

    public RefreshTokenTimeUpException() {
        super(MESSAGE);
    }

    public RefreshTokenTimeUpException(String message) {
        super(message);
    }

    public RefreshTokenTimeUpException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenTimeUpException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
