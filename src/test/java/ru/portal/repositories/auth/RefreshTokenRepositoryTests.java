package ru.portal.repositories.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/sql/user_test.sql", "/sql/auth/refresh_token_repository_test.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class RefreshTokenRepositoryTests {

    private static final String TOKEN = "cbbnb42fe1f4fgd697143da246a4132b";

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenRepositoryTests(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Test
    void testFindConfirmationTokenByToken() {

        var token = refreshTokenRepository.findByToken(TOKEN)
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertEquals(1, token.getId(),
                        "Идентификаторы должны совпадать"),
                () -> assertEquals("cbbnb42fe1f4fgd697143da246a4132b", token.getToken(),
                        "Токен должен совпадать"),
                () -> assertEquals(12000, token.getLifetime(),
                        "Время жизни должно совпадать"),
                () -> assertEquals(1, token.getUser().getId(),
                        "Идентификатор пользователя должен совпадать")
        );

    }

    @Test
    void testDeleteConfirmationTokenByToken() {
        refreshTokenRepository.deleteByToken(TOKEN);
        var deleteToken = refreshTokenRepository.findByToken(TOKEN);
        assertFalse(deleteToken.isPresent(), "Токена не должно существовать");
    }

}
