package ru.portal.security.services.exception;

/**
 * Исключение бросаеться если время жизни токена истекло.<br>
 * Сообщение по умолчанию {@value MESSAGE}
 *
 * @author Федорышин К.В.
 */
public class TokenTimeExpiredException extends RuntimeException {

    static String MESSAGE = "Время жизни токена истекло";

    public TokenTimeExpiredException() {
        super(MESSAGE);
    }

    public TokenTimeExpiredException(String message) {
        super(message);
    }

    public TokenTimeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenTimeExpiredException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
