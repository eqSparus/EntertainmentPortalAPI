package ru.portal.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import ru.portal.entities.Role;
import ru.portal.entities.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = "/sql/user_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
    void testFindUserByUsernameAndFindUserByEmail() {

        var userUsername = userRepository.findByUsername("Sparus")
                .orElseThrow(IllegalArgumentException::new);

        assertEquals(1, userUsername.getId(), "Идентификаторы должны совпадать");
        assertEquals("Sparus", userUsername.getUsername(), "Имена должны совпадать");
        assertEquals("rf1991@mail.ru", userUsername.getEmail(), "Адреса должны совпадать");
        assertEquals(Role.USER, userUsername.getRole(), "Адреса должны совпадать");
        assertEquals(Status.ACTIVE, userUsername.getStatus(), "Статусы должны совпадать");
        assertTrue(encoder.matches("rootroot", userUsername.getPassword()), "Необработанный пароль " +
                "должен совпадать с закодированым");

        var userEmail = userRepository.findByEmail("rf1991@mail.ru")
                .orElseThrow(IllegalArgumentException::new);

        assertEquals(1, userEmail.getId(), "Идентификаторы должны совпадать");
        assertEquals("Sparus", userEmail.getUsername(), "Имена должны совпадать");
        assertEquals("rf1991@mail.ru", userEmail.getEmail(), "Адреса должны совпадать");
        assertEquals(Role.USER, userEmail.getRole(), "Адреса должны совпадать");
        assertEquals(Status.ACTIVE, userEmail.getStatus(), "Статусы должны совпадать");
        assertTrue(encoder.matches("rootroot", userEmail.getPassword()), "Необработанный пароль " +
                "должен совпадать с закодированым");
    }

    @Test
    void testExistsEmailAndUsername() {

        var usernameExist = userRepository.existsByEmail("rf1991@mail.ru");
        assertTrue(usernameExist, "Адрес должен существовать");

        var emailExist = userRepository.existsByUsername("Sparus");
        assertTrue(emailExist, "Имя должно существовать");
    }

}
