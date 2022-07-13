package ru.portal.security.services.exception;

/**
 * Исключение бросаеться если время жизни токена истекло.<br>
 * Сообщение по умолчанию {@value MESSAGE}
 *
 * @author Федорышин К.В.
 */
public class ConfirmationTokenTimeExpiredException extends RuntimeException {

    static String MESSAGE = "Время жизни токена истекло";

    public ConfirmationTokenTimeExpiredException() {
        super(MESSAGE);
    }

    public ConfirmationTokenTimeExpiredException(String message) {
        super(message);
    }

    public ConfirmationTokenTimeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfirmationTokenTimeExpiredException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
