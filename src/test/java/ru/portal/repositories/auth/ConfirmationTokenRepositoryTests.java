package ru.portal.repositories.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/sql/user/user_await_test.sql", "/sql/auth/confirmation_token_valid_test.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DataJpaTest
class ConfirmationTokenRepositoryTests {

    private static final String TOKEN = "cb0eb42fe1fd4d6397143da246a4132b";
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenRepositoryTests(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }


    @Test
    void testFindConfirmationTokenByToken() {

        var token = confirmationTokenRepository.findByToken(TOKEN)
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertEquals(1, token.getId(),
                        "Идентификаторы должны совпадать"),
                () -> assertEquals(TOKEN, token.getToken(),
                        "Токен должен совпадать"),
                () -> assertEquals(4_088_238_925_000L, token.getLifetime(),
                        "Время жизни должно совпадать"),
                () -> assertEquals(1, token.getUser().getId(),
                        "Идентификатор пользователя должен совпадать")
        );


    }

    @Test
    void testDeleteConfirmationTokenByToken() {
        confirmationTokenRepository.deleteByToken(TOKEN);
        var deleteToken = confirmationTokenRepository.findByToken(TOKEN);
        assertFalse(deleteToken.isPresent(), "Токена не должно существовать");
    }
}
