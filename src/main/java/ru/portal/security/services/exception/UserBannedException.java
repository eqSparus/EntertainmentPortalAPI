package ru.portal.security.services.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserBannedException extends RuntimeException {

    static String MESSAGE = "Пользователь заблокирован на один чаc";

    public UserBannedException() {
        super(MESSAGE);
    }

    public UserBannedException(String message) {
        super(message);
    }

    public UserBannedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBannedException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
