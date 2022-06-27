package ru.portal.security.services.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserExistsException extends RuntimeException {

    static String MESSAGE = "Такой пользователь уже существует!";
    public static String LOGIN_EXIST = "Пользователь с таким именем уже существует!";
    public static String EMAIL_EXIST = "Пользователь с таким адресом уже существует!";

    public UserExistsException() {
        super(MESSAGE);
    }

    public UserExistsException(String message) {
        super(message);
    }

    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserExistsException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
