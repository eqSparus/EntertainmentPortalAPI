package ru.portal.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.portal.entities.User;
import ru.portal.repositories.auth.RefreshTokenRepository;
import ru.portal.security.services.exception.RefreshTokenNotExistsException;
import ru.portal.security.services.exception.RefreshTokenTimeUpException;

import java.time.Instant;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenRefreshServiceImplTests {

    private static final Pattern REGEX_TOKEN_WITH_BEARER = Pattern
            .compile("Bearer_([\\w_=]+)\\.([\\w_=]+)\\.([\\w_\\-\\+\\/=]*)");
    private static final Pattern REGEX_REFRESH_TOKEN = Pattern.compile("\\w{32}");

    private static final String REFRESH_TOKEN = "cbbnb42fe1f4fgd697143da246a4132b";

    private final TokenRefreshService tokenRefreshService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public TokenRefreshServiceImplTests(TokenRefreshService tokenRefreshService,
                                        RefreshTokenRepository refreshTokenRepository) {
        this.tokenRefreshService = tokenRefreshService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Sql(scripts = {"/sql/user_test.sql", "/sql/auth/refresh_token_repository_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRefreshToken() {

        var token = tokenRefreshService.refreshToken(REFRESH_TOKEN);

        var refreshToken = refreshTokenRepository.findByToken(token.getRefreshToken())
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertEquals(token.getRefreshToken(), refreshToken.getToken(),
                        "Токены обновления должны совпадать"),
                () -> assertTrue(Instant.now().isBefore(Instant.ofEpochMilli(refreshToken.getLifetime())),
                        "Проверяет что время жизни токена обновления больше текущего момента времени"),
                () -> assertTrue(token.getRefreshToken().matches(REGEX_REFRESH_TOKEN.toString()),
                        ""),
                () -> assertTrue(token.getAuthorization().matches(REGEX_TOKEN_WITH_BEARER.toString()),
                        "")
        );
    }

    @Sql(scripts = "/sql/user_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRefreshTokenNotExists() {

        var exception = assertThrows(RefreshTokenNotExistsException.class,
                () -> tokenRefreshService.refreshToken(REFRESH_TOKEN),
                "Токен обновления не должен существовать");

        assertNotNull(exception.getMessage(), "Сообщение должно присутствовать");
    }

    @Sql(scripts = {"/sql/user_test.sql", "/sql/auth/refresh_token_repository_not_valid_time_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRefreshTokenNotValidTime() {

        var exception = assertThrows(RefreshTokenTimeUpException.class,
                () -> tokenRefreshService.refreshToken(REFRESH_TOKEN),
                "Исключения время жизни токена истекло должно выбрасываться");

        assertNotNull(exception.getMessage(), "Сообщение должно присутствовать");
    }

    @Sql(scripts = "/sql/user_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testAddRefreshToken() {

        var user = User.builder()
                .id(1L)
                .build();

        var refreshToken = tokenRefreshService.addRefreshToken(user)
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertTrue(refreshToken.getToken().matches(REGEX_REFRESH_TOKEN.toString()),
                        "Токен должен соответствовать регулярному выражению"),
                () -> assertTrue(Instant.now().isBefore(Instant.ofEpochMilli(refreshToken.getLifetime())),
                        "Время жизни токена больше текущего момента времени"),
                () -> assertEquals(user.getId(), refreshToken.getUser().getId(),
                        "Идентификаторы пользователя должны совпадать")
        );
    }

    @Sql(scripts = {"/sql/user_test.sql", "/sql/auth/refresh_token_repository_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testDeleteRefreshToken() {
        tokenRefreshService.deleteRefreshToken(REFRESH_TOKEN);
        var deleteToken = refreshTokenRepository.findByToken(REFRESH_TOKEN);
        assertFalse(deleteToken.isPresent(), "Токена не должно существовать");
    }

    @Sql(scripts = {"/sql/user_test.sql", "/sql/auth/refresh_token_repository_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testIsLifetimeRefreshTokenTrue() {
        var token = refreshTokenRepository.findByToken(REFRESH_TOKEN);
        assertTrue(tokenRefreshService.isLifetimeRefreshToken(token.orElseThrow(IllegalArgumentException::new)),
                "Токен должен быть актуален");
    }

    @Sql(scripts = {"/sql/user_test.sql", "/sql/auth/refresh_token_repository_not_valid_time_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testIsLifetimeRefreshTokenFalse() {
        var token = refreshTokenRepository.findByToken(REFRESH_TOKEN);
        assertFalse(tokenRefreshService.isLifetimeRefreshToken(token.orElseThrow(IllegalArgumentException::new)),
                "Токен должен быть неактуален");
    }
}
