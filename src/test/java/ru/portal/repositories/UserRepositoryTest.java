package ru.portal.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.portal.entities.Role;
import ru.portal.entities.Status;
import ru.portal.entities.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Test
    void saveUserTest() {

        var user = User.builder()
                .username("Sparus")
                .email("rf1991@mail.ru")
                .password(encoder.encode("password"))
                .role(Role.USER)
                .status(Status.AWAIT)
                .build();

        var newUser = userRepository.save(user);

        assertEquals("Sparus", newUser.getUsername(), "Имена должны совпадать");
        assertEquals("rf1991@mail.ru", newUser.getEmail(), "Адреса должны совпадать");
        assertEquals(Role.USER, newUser.getRole(), "Адреса должны совпадать");
        assertEquals(Status.AWAIT, newUser.getStatus(), "Статусы должны совпадать");
        assertTrue(encoder.matches("password", newUser.getPassword()), "Необработанный пароль " +
                "должен совпадать с закодированым");
    }

}
