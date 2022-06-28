package ru.portal.security.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.portal.entities.Status;
import ru.portal.entities.dto.response.DtoSuccessAuthResponse;
import ru.portal.repositories.auth.ConfirmationTokenRepository;
import ru.portal.security.services.ConfirmationService;
import ru.portal.security.services.UserService;
import ru.portal.security.services.exception.ConfirmationTokenNotExistException;
import ru.portal.security.services.exception.TokenTimeExpiredException;

import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * Реализация интерфейса {@link ConfirmationService} для подтверждения электронной
 * почты.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ConfirmationServiceImpl implements ConfirmationService {

    UserService userService;
    ConfirmationTokenRepository confirmationRepository;

    @Autowired
    public ConfirmationServiceImpl(UserService userService,
                                   ConfirmationTokenRepository confirmationRepository) {
        this.userService = userService;
        this.confirmationRepository = confirmationRepository;
    }

    /**
     * Проверяет токен на существования и время жизни пользователя, в случае успеха
     * удаляет токен подтверждения из БД и активирует учетную запись пользователя.
     *
     * @param token подтверждения электронной почты.
     * @return ответ подтверждения.
     * @throws ConfirmationTokenNotExistException бросаеться если токена подтверждение не существутет.
     * @throws TokenTimeExpiredException бросаеться если срок действия токена истек.
     * @see ru.portal.entities.dto.response.DtoSuccessAuthResponse
     */
    @Transactional
    @Override
    public DtoSuccessAuthResponse confirmation(@NonNull String token)
            throws ConfirmationTokenNotExistException, TokenTimeExpiredException {

        var confirmationToken = confirmationRepository.findByToken(token)
                .orElseThrow(ConfirmationTokenNotExistException::new);

        if (confirmationToken.getLifetime() >= Instant.now().toEpochMilli()) {
            userService.updateStatus(Status.ACTIVE, confirmationToken.getUser());
            confirmationRepository.deleteByToken(token);
            return DtoSuccessAuthResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Пользователь активирован")
                    .timestamp(OffsetDateTime.now())
                    .userId(confirmationToken.getUser().getId())
                    .build();
        } else {
            confirmationRepository.deleteByToken(token);
            throw new TokenTimeExpiredException();
        }

    }
}
