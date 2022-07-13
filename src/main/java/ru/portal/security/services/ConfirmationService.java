package ru.portal.security.services;

import org.springframework.lang.NonNull;
import ru.portal.entities.dto.response.auth.DtoSuccessRegResponse;
import ru.portal.security.services.exception.ConfirmationTokenNotExistException;
import ru.portal.security.services.exception.ConfirmationTokenTimeExpiredException;

/**
 * Токен подтверждения электронной почты.
 *
 * @author Федорышин К.В.
 */
public interface ConfirmationService {

    /**
     * Проверяет токен подтверждения электронной почты и возвращает ответ.
     *
     * @param token подтверждения электронной почты.
     * @return ответ подтверждения.
     * @throws ConfirmationTokenNotExistException бросаеться если токена подтверждение не существутет.
     * @throws ConfirmationTokenTimeExpiredException бросаеться если срок действия токена истек.
     * @see DtoSuccessRegResponse
     */
    DtoSuccessRegResponse confirmation(@NonNull String token)
            throws ConfirmationTokenNotExistException, ConfirmationTokenTimeExpiredException;

}
