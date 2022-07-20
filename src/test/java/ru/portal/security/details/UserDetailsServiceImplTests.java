package ru.portal.security.details;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import ru.portal.entities.Role;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "/sql/user_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
class UserDetailsServiceImplTests {

    private static final String USERNAME = "Sparus";
    private final UserDetailsService userDetailsService;


    @Autowired
    public UserDetailsServiceImplTests(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Test
    void testLoadUserByUsername() {

        var user = assertDoesNotThrow(() -> userDetailsService.loadUserByUsername(USERNAME),
                "Исключение не выброшено");

        assertAll(
                () -> assertEquals("Sparus", user.getUsername(),
                        "Имена должны совпадать"),
                () -> assertEquals(Collections.singletonList(new SimpleGrantedAuthority(Role.USER.toString())),
                        user.getAuthorities(), "Привилегии должны совпадать")
        );
    }

    @Test
    void testLoadUserByUsernameFailed() {

        var exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("SSSS"),
                "Бросок исключения не произошел");

        assertNotNull(exception.getMessage(), "Сообщение должно присутствовать");
    }


}
