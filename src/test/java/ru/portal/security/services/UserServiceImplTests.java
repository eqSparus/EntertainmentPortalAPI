package ru.portal.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import ru.portal.entities.Role;
import ru.portal.entities.Status;
import ru.portal.entities.dto.request.auth.DtoUserRequest;
import ru.portal.repositories.UserRepository;
import ru.portal.security.services.exception.IncorrectCredentialsException;
import ru.portal.security.services.exception.UserBannedException;
import ru.portal.security.services.exception.UserExistsException;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@RecordApplicationEvents
@SpringBootTest
class UserServiceImplTests {

    private static final Pattern REGEX_TOKEN_WITH_BEARER = Pattern
            .compile("Bearer_([\\w_=]+)\\.([\\w_=]+)\\.([\\w_\\-\\+\\/=]*)");

    private static final Pattern REGEX_REFRESH_TOKEN = Pattern.compile("\\w{32}");


    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImplTests(UserService userService,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRegistrationUser() {

        var dtoUser = new DtoUserRequest("Sparus", "rf1991@mail.ru", "rootroot");

        var response = userService.registrationUser(dtoUser);

        var user = userRepository.findByUsername(dtoUser.getUsername())
                .orElseThrow(IllegalArgumentException::new);

        assertAll(
                () -> assertEquals(Status.AWAIT, user.getStatus(),
                        "Статус пользователя должен быть \"AWAIT\""),
                () -> assertEquals(dtoUser.getUsername(), user.getUsername(),
                        "Имя пользователя должно совпадать"),
                () -> assertEquals(dtoUser.getEmail(), user.getEmail(),
                        "Адрес пользователя должен совпадать"),
                () -> assertEquals(Role.USER, user.getRole(),
                        "Роль пользователя должно быть \"USER\""),
                () -> assertTrue(passwordEncoder.matches(dtoUser.getPassword(), user.getPassword()),
                        "Закодированный пароль должен совпадать с необработанным паролем")
        );

        assertEquals(201, response.getStatus(), "Ответ должен быть 201");
        assertNotNull(response.getMessage(), "Сообщение должно существовать");
    }

    @Sql(scripts = "/sql/user/user_await_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRegistrationUserExists() {

        var dtoUser = new DtoUserRequest("Sparus", "rf1991@mail.ru", "rootroot");

        var exception = assertThrows(UserExistsException.class,
                () -> userService.registrationUser(dtoUser),
                "Должно бросаться исключение пользователь существует");

        assertNotNull(exception.getMessage(), "Сообщение должно присутствовать");
    }

    @Sql(scripts = {"/sql/user/user_active_test.sql", "/sql/auth/login_attempt_valid_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testLogin() {

        var dtoUser = new DtoUserRequest("Sparus", "", "rootroot");

        var response = userService.login(dtoUser);

        assertTrue(response.getAuthorization().matches(REGEX_TOKEN_WITH_BEARER.toString()),
                "JWT токен должен соответствовать регулярному выражению");
        assertTrue(response.getRefreshToken().matches(REGEX_REFRESH_TOKEN.toString()),
                "Токен обновления должен соответствовать регулярному выражению");
    }

    @Sql(scripts = {"/sql/user/user_active_test.sql", "/sql/auth/login_attempt_valid_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testLoginIncorrectCredentials() {

        var dtoUser = new DtoUserRequest("Sparus", "", "sdfdsfdsf");

        var exception = assertThrows(IncorrectCredentialsException.class,
                () -> userService.login(dtoUser),
                "Исключение некорректные данные должны бросаться");

        assertNotNull(exception.getMessage(), "Сообщение должно присутствовать");
    }

    @Sql(scripts = {"/sql/user/user_block_test.sql", "/sql/auth/login_attempt_block_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testLoginUserBanned() {

        var dtoUser = new DtoUserRequest("Sparus", "", "rootroot");

        var exception = assertThrows(UserBannedException.class,
                () -> userService.login(dtoUser),
                "Исключение пользователь заблокирован должны бросаться");

        assertNotNull(exception.getMessage(), "Исключение некорректные данные должны бросаться");
    }

    @Sql(scripts = {"/sql/user/user_block_test.sql", "/sql/auth/login_attempt_valid_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testIsTimeBlockTrue() {
        var user = userRepository.findByUsername("Sparus")
                .orElseThrow(IllegalArgumentException::new);
        var isTimeBlock = userService.isTimeBlock(user);
        assertTrue(isTimeBlock, "Время блокировки должно быть закончено");
    }

    @Sql(scripts = {"/sql/user/user_block_test.sql", "/sql/auth/login_attempt_block_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testIsTimeBlockFalse() {
        var user = userRepository.findByUsername("Sparus")
                .orElseThrow(IllegalArgumentException::new);
        var isTimeBlock = userService.isTimeBlock(user);
        assertFalse(isTimeBlock, "Время блокировки должно быть не закончено");
    }

    @Sql(scripts = "/sql/user/user_block_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testUpdateStatus() {

        var user = userRepository.findByUsername("Sparus")
                .orElseThrow(IllegalArgumentException::new);

        userService.updateStatus(Status.ACTIVE, user);
        assertEquals(Status.ACTIVE, user.getStatus(), "Пользователь должен быть активирован");
    }

    @Sql(scripts = "/sql/user/user_await_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testCheckUsernameExist() {
        var exist = userService.checkUsername("Sparus");
        assertTrue(exist,"Пользователь должен существовать");
    }

    @Test
    void testCheckUsernameNotExist() {
        var exist = userService.checkUsername("Sparus");
        assertFalse(exist,"Пользователь не должен существовать");
    }

    @Sql(scripts = "/sql/user/user_await_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testCheckEmailExist() {
        var exist = userService.checkEmail("rf1991@mail.ru");
        assertTrue(exist,"Пользователь должен существовать");
    }

    @Test
    void testCheckEmailNotExist() {
        var exist = userService.checkEmail("rf1991@mail.ru");
        assertFalse(exist,"Пользователь не должен существовать");
    }

}
