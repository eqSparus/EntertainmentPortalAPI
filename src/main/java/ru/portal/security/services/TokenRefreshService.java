package ru.portal.security.services;

import org.springframework.lang.NonNull;
import ru.portal.entities.User;
import ru.portal.entities.auth.RefreshToken;
import ru.portal.entities.dto.response.auth.DtoAuthenticationResponse;

import java.util.Optional;

/**
 * Интерфейс для взаимодействия с токенами обновления.
 *
 * @author Федорышин К.В.
 */
public interface TokenRefreshService {

    /**
     * Обновляет токен доступа и токен обновления и возвращает.
     * ответ
     *
     * @param refreshToken токен обновления.
     * @return ответ обновления токена.
     * @see DtoAuthenticationResponse
     */
    @NonNull
    DtoAuthenticationResponse refreshToken(@NonNull String refreshToken);

    /**
     * Добавляет токен обновления к пользователю и возращает его.
     *
     * @param user пользователь.
     * @return токен обновления.
     * @see ru.portal.entities.auth.RefreshToken
     */
    Optional<RefreshToken> addRefreshToken(@NonNull User user);

    /**
     * Удаляет токен обновления из хранилища.
     *
     * @param token токен обновления.
     */
    void deleteRefreshToken(@NonNull String token);

    boolean isLifetimeRefreshToken(@NonNull RefreshToken token);
}
