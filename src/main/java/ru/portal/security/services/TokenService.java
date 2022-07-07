package ru.portal.security.services;

import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;

/**
 * Интерфейс с методами для взаимодействия с токенами доступа
 *
 * @author Федорышин К.В.
 */
public interface TokenService {

    /**
     * Создает токен доступа со стандартным временем
     * и именем пользователя.
     *
     * @param username имя пользователя.
     * @return токена доступа с "Bearer_".
     */
    @NonNull
    String createToken(@NonNull String username);

    /**
     * Создает токен доступа и именем пользователя.
     *
     * @param username имя пользователя.
     * @param time     время жизни токена.
     * @return токен доступа с "Bearer_".
     */
    @NonNull
    String createToken(@NonNull String username, @NonNull Long time);

    /**
     * Извлекает из запроса токен доступа и удаляет "Bearer_".
     *
     * @param request запрос к серверу.
     * @return токен доступа без "Bearer_".
     */
    @NonNull
    String getToken(@NonNull HttpServletRequest request);

    /**
     * Удаляет из токена "Bearer_".
     *
     * @param token токен доступа.
     * @return токен без "Bearer_".
     */
    @NonNull
    String getToken(@NonNull String token);

    /**
     * Проверяет время жизни токена.
     *
     * @param token токен доступа.
     * @return true если токен действительный иначе false.
     */
    boolean isValidToken(@NonNull String token);

    /**
     * Получает имя пользователя пользователя из токена.
     *
     * @param token токен доступа.
     * @return имя пользователя.
     */
    @NonNull
    String getUsername(@NonNull String token);

}
