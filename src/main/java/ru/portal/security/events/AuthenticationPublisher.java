package ru.portal.security.events;

import org.springframework.lang.NonNull;
import ru.portal.entities.User;
import ru.portal.entities.dto.request.auth.DtoUserRequest;

/**
 * Интерфейс предоставляет методы связанные с событиями регистрацией
 * и авторизацией пользователя.
 *
 * @author Федорышин К.В.
 */
public interface AuthenticationPublisher {

    /**
     * Метод издает события до авторизации пользователя.
     *
     * @param request тело запроса авторизации.
     */
    void publishEventLogin(@NonNull DtoUserRequest request);

    /**
     * Метод издает события после регистрации пользователя.
     *
     * @param user зарегистрированный пользователь.
     */
    void publishEventRegistration(@NonNull User user);

}
