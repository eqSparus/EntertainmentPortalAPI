package ru.portal.rest.auth;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.portal.entities.dto.Views;
import ru.portal.entities.dto.response.DtoAuthenticationResponse;
import ru.portal.security.services.TokenRefreshService;
import ru.portal.security.services.UserService;

import java.time.OffsetDateTime;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class TokenRest {

    UserService userService;

    TokenRefreshService refreshService;

    @Autowired
    public TokenRest(UserService userService, TokenRefreshService refreshService) {
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
    @JsonView(Views.RefreshToken.class)
    public DtoAuthenticationResponse refreshToken(
            @RequestHeader(name = "RefreshToken") String refreshToken
    ) {
        return refreshService.refreshToken(refreshToken);
    }

    @GetMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> test() {
        return Map.of("time", OffsetDateTime.now().toString());
    }
}
