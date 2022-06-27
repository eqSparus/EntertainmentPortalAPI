package ru.portal.security.services.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Исключение бросаеться если пользователь уже существует.<br>
 * Сообщение по умолчанию {@value MESSAGE}<br>
 * Сообщение имя существует {@value LOGIN_EXIST}<br>
 * Сообщение почта существует {@value EMAIL_EXIST}<br>
 *
 * @author Федорышин К.В.
 */
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
