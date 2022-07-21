package ru.portal.repositories.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.portal.entities.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Sql(scripts = {"/sql/user/user_await_test.sql", "/sql/auth/login_attempt_valid_test.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DataJpaTest
class LoginAttemptRepositoryTests {

    private final LoginAttemptRepository loginAttemptRepository;


    @Autowired
    public LoginAttemptRepositoryTests(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Test
    void testFindAttemptByUser() {

        var user = User.builder()
                .id(1L)
                .build();

        var attempt = loginAttemptRepository.findByUser(user)
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertEquals(1, attempt.getId(),
                        "Идентификаторы должны совпадать"),
                () -> assertEquals(2, attempt.getNumberAttempt(),
                        "Количество попыток должно быть равно 2"),
                () -> assertEquals(0, attempt.getLockTime(),
                        "Время жизни токена должно быть одинаково"),
                () -> assertEquals(user.getId(), attempt.getUser().getId(),
                        "Идентификаторы пользователя должны совпадать")
        );


    }
}
