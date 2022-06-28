package ru.portal.security.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.portal.entities.User;
import ru.portal.entities.auth.RefreshToken;
import ru.portal.entities.dto.response.DtoAuthenticationResponse;
import ru.portal.repositories.auth.RefreshTokenRepository;
import ru.portal.security.services.TokenRefreshService;
import ru.portal.security.services.TokenService;
import ru.portal.security.services.exception.RefreshTokenNotExistsException;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация интерфейса {@link TokenRefreshService} для токенов обновления.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class TokenRefreshServiceImpl implements TokenRefreshService {

    RefreshTokenRepository refreshTokenRepository;
    TokenService tokenService;

    @Autowired
    public TokenRefreshServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   TokenService tokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
    }

    /**
     * Проверяет токен обновления на существование, в случае усеха создает новый токен доступа
     * и токен обновления и возвращает его в ответе.
     *
     * @param refreshToken токен обновления.
     * @return ответ обновления токена.
     * @see ru.portal.entities.dto.response.DtoAuthenticationResponse
     */
    @Transactional(rollbackFor = RefreshTokenNotExistsException.class)
    @NonNull
    @Override
    public DtoAuthenticationResponse refreshToken(@NonNull String refreshToken) {
        var refToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(RefreshTokenNotExistsException::new);

        var newRefreshToken = UUID.randomUUID().toString();
        refToken.setToken(newRefreshToken);

        var user = refToken.getUser();

        var token = tokenService.createToken(user.getUsername());

        refreshTokenRepository.save(refToken);

        return DtoAuthenticationResponse.builder()
                .authorization(token)
                .refreshToken(newRefreshToken)
                .timestamp(ZonedDateTime.now())
                .build();
    }


    /**
     * Добавляет токен обновления к пользователю и возвращает его.
     *
     * @param user пользователь.
     * @return токен обновления {@link RefreshToken}.
     */
    @Override
    public Optional<RefreshToken> addRefreshToken(@NonNull User user) {
        var refreshToken = UUID.randomUUID().toString();

        var tokenRefresh = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .build();

        return Optional.of(refreshTokenRepository.save(tokenRefresh));
    }

    /**
     * Удаляет токен обновления из хранилища.
     *
     * @param token токен обновления.
     */
    @Transactional
    @Override
    public void deleteRefreshToken(@NonNull String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
