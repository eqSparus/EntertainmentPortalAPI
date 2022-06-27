package ru.portal.security.services.exception;

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
