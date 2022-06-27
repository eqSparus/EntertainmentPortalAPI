package ru.portal.security.services;

import org.springframework.lang.NonNull;
import ru.portal.entities.User;
import ru.portal.entities.auth.RefreshToken;
import ru.portal.entities.dto.response.DtoAuthenticationResponse;

import java.util.Optional;

public interface TokenRefreshService {

    @NonNull
    DtoAuthenticationResponse refreshToken(@NonNull String refreshToken);

    Optional<RefreshToken> addRefreshToken(@NonNull User user);

    void deleteRefreshToken (@NonNull String token);

}
