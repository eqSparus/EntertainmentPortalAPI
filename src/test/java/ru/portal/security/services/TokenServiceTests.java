package ru.portal.security.services;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(value = "classpath:application-test.yaml")
class TokenServiceTests {

    private static final Pattern REGEX_TOKEN_WITH_BEARER = Pattern
            .compile("Bearer_([\\w_=]+)\\.([\\w_=]+)\\.([\\w_\\-\\+\\/=]*)");
    private static final Pattern REGEX_TOKEN = Pattern.compile("([\\w_=]+)\\.([\\w_=]+)\\.([\\w_\\-\\+\\/=]*)");
    private static final String USERNAME = "Sparus";

    private final TokenService tokenService;
    private final JwtParser jwtParser;


    @Autowired
    public TokenServiceTests(TokenService tokenService,
                             Environment environment) {
        this.tokenService = tokenService;
        var secretKey = Keys.hmacShaKeyFor(environment.getRequiredProperty("security.token.key")
                .getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
    }

    @Test
    void testCreateToken() {
        var token = tokenService.createToken(USERNAME);
        assertTrue(token.matches(REGEX_TOKEN_WITH_BEARER.toString()),
                "Токен должен соответствовать регулярному выражению");

        var validTime = jwtParser.parseClaimsJws(token.substring(7))
                .getBody().getExpiration();

        assertNotNull(validTime, "Время жизни токена должно присутствовать");
        assertTrue(Instant.now().isBefore(validTime.toInstant()),
                "Проверяет что время жизни токена больше текущего момента времени");
    }

    @Test
    void testCreateTokenWithValidTime() {
        var token = tokenService.createToken(USERNAME, 10_000L);
        assertTrue(token.matches(REGEX_TOKEN_WITH_BEARER.toString()),
                "Токен должен соответствовать регулярному выражению");

        var validTime = jwtParser.parseClaimsJws(token.substring(7))
                .getBody().getExpiration();

        assertNotNull(validTime, "Время жизни токена должно присутствовать");
        assertTrue(Instant.now().isBefore(validTime.toInstant()),
                "Время жизни токена больше текущего момента времени");
    }

    @Test
    void testGetToken() {
        var tokenWithBearer = tokenService.createToken(USERNAME);
        var token = tokenService.getToken(tokenWithBearer);
        assertTrue(token.matches(REGEX_TOKEN.toString()),
                "Токен должен соответствовать регулярному выражению");
    }

    @Test
    void testIsValidTokenTrue() {
        var token = tokenService.createToken(USERNAME);
        var isValid = tokenService.isValidToken(token.substring(7));
        assertTrue(isValid, "Токен должен быть действительным");
    }

    @Test
    void testIsValidTokenFalse() {
        var token = tokenService.createToken(USERNAME, 0L);
        var isValid = tokenService.isValidToken(token.substring(7));
        assertFalse(isValid, "Токен должен быть недействительным");
    }

    @Test
    void testGetUsername() {
        var token = tokenService.createToken(USERNAME);
        var username = tokenService.getUsername(token.substring(7));
        assertEquals("Sparus", username, "Имена должны совпадать");
    }


}
