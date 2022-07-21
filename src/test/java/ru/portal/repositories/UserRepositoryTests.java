package ru.portal.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import ru.portal.entities.Role;
import ru.portal.entities.Status;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "/sql/user/user_await_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DataJpaTest
class UserRepositoryTests {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Test
    void testFindUserByUsername() {

        var user = userRepository.findByUsername("Sparus")
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertEquals(1, user.getId(),
                        "Идентификаторы должны совпадать"),
                () -> assertEquals("Sparus", user.getUsername(),
                        "Имена должны совпадать"),
                () -> assertEquals("rf1991@mail.ru", user.getEmail(),
                        "Адреса должны совпадать"),
                () -> assertEquals(Role.USER, user.getRole(),
                        "Адреса должны совпадать"),
                () -> assertEquals(Status.AWAIT, user.getStatus(),
                        "Статусы должны совпадать"),
                () -> assertTrue(encoder.matches("rootroot", user.getPassword()),
                        "Необработанный пароль должен совпадать с закодированым")
        );

    }

    @Test
    void testFindUserByEmail() {
        var user = userRepository.findByEmail("rf1991@mail.ru")
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertEquals(1, user.getId(),
                        "Идентификаторы должны совпадать"),
                () -> assertEquals("Sparus", user.getUsername(),
                        "Имена должны совпадать"),
                () -> assertEquals("rf1991@mail.ru", user.getEmail(),
                        "Адреса должны совпадать"),
                () -> assertEquals(Role.USER, user.getRole(),
                        "Адреса должны совпадать"),
                () -> assertEquals(Status.AWAIT, user.getStatus(),
                        "Статусы должны совпадать"),
                () -> assertTrue(encoder.matches("rootroot", user.getPassword()),
                        "Необработанный пароль должен совпадать с закодированым")
        );
    }

    @Test
    void testExistsEmail() {
        var exists = userRepository.existsByUsername("Sparus");
        assertTrue(exists, "Имя должно существовать");
    }

    @Test
    void testExistsUsername() {
        var exists = userRepository.existsByEmail("rf1991@mail.ru");
        assertTrue(exists, "Адрес должен существовать");
    }

}
