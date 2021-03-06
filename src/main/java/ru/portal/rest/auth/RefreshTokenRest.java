package ru.portal.rest.auth;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.portal.entities.dto.response.auth.DtoAuthenticationResponse;
import ru.portal.security.services.TokenRefreshService;
import ru.portal.security.services.UserService;

/**
 * Конечные точки для токенов обновления.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class RefreshTokenRest {

    UserService userService;

    TokenRefreshService refreshService;

    @Autowired
    public RefreshTokenRest(UserService userService, TokenRefreshService refreshService) {
        this.userService = userService;
        this.refreshService = refreshService;
    }

    @PostMapping(path = "/exit")
    public ResponseEntity<String> logout(
            @RequestHeader(name = "RefreshToken") String refreshToken
    ) {
        userService.logout(refreshToken);
        return ResponseEntity.ok("Logout success");
    }

    @PostMapping(path = "/refreshtoken", produces = MediaType.APPLICATION_JSON_VALUE)
    public DtoAuthenticationResponse refreshToken(
            @RequestHeader(name = "RefreshToken") String refreshToken
    ) {
        return refreshService.refreshToken(refreshToken);
    }

}
