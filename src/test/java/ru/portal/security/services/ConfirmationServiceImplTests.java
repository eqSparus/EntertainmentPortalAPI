package ru.portal.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.portal.entities.Status;
import ru.portal.repositories.UserRepository;
import ru.portal.repositories.auth.ConfirmationTokenRepository;
import ru.portal.security.services.exception.ConfirmationTokenNotExistException;
import ru.portal.security.services.exception.ConfirmationTokenTimeExpiredException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConfirmationServiceImplTests {

    private static final String CONFIRMATION_TOKEN = "cb0eb42fe1fd4d6397143da246a4132b";

    private final ConfirmationService confirmationService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public ConfirmationServiceImplTests(ConfirmationService confirmationService,
                                        ConfirmationTokenRepository confirmationTokenRepository,
                                        UserRepository userRepository) {
        this.confirmationService = confirmationService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
    }

    @Sql(scripts = {"/sql/user_test.sql", "/sql/auth/confirmation_token_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testConfirmation() {

        var response = confirmationService.confirmation(CONFIRMATION_TOKEN);

        var user = userRepository.findByUsername("Sparus")
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertEquals(Status.ACTIVE, user.getStatus(),
                        "Статус пользователя должен быть активен"),
                () -> assertEquals(200, response.getStatus(),
                        "Статус ответа должен быть 200"),
                () -> assertEquals(Optional.empty(),
                        confirmationTokenRepository.findByToken(CONFIRMATION_TOKEN),
                        "Токен не должен быть найден")
        );
    }

    @Sql(scripts = "/sql/user_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testConfirmationNotExistsToken() {

        var exception = assertThrows(ConfirmationTokenNotExistException.class,
                () -> confirmationService.confirmation(CONFIRMATION_TOKEN),
                "Должно выбрасываться исключение отсутствия токена");

        assertNotNull(exception.getMessage(), "Сообщение должно присутствовать");
    }

    @Sql(scripts = {"/sql/user_test.sql", "/sql/auth/confirmation_token_not_valid_time_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void testConfirmationNotValidTime() {
        var exception = assertThrows(ConfirmationTokenTimeExpiredException.class,
                () -> confirmationService.confirmation(CONFIRMATION_TOKEN),
                "Должно выбрасываться исключение время жизни токена истекло");
        var token = confirmationTokenRepository.findByToken(CONFIRMATION_TOKEN);
        assertNotNull(exception.getMessage(), "Сообщение должно присутствовать");
        assertFalse(token.isPresent(), "Токена не должно существовать");
    }


}
