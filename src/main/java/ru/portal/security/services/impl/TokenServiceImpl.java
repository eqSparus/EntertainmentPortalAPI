package ru.portal.security.services.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.portal.security.services.TokenService;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Реализация интерфейса {@link TokenService} для
 * токенов доступа.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${security.token.validTimeInSecond}")
    Long validTime;

    @Value("${security.token.bearer}")
    String bearer;

    @Value("${security.token.headerAuthorizationName}")
    String headerToken;

    final SecretKey secretKey;

    public TokenServiceImpl(Environment environment) {
        this.secretKey = Keys.hmacShaKeyFor(environment.getRequiredProperty("security.token.key")
                .getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Создает токен доступа со стандартным временем и именем пользователя.
     *
     * @param username имя пользователя.
     * @return токена доступа с "Bearer_".
     */
    @NonNull
    @Override
    public String createToken(@NonNull String username) {
        return createToken(username, validTime);
    }

    /**
     * Создает токен доступа и именем пользователя.
     *
     * @param username имя пользователя.
     * @param time     время жизни токена.
     * @return токена доступа с "Bearer_".
     */
    @NonNull
    @Override
    public String createToken(@NonNull String username, @NonNull Long time) {
        var timeStart = Instant.now();
        var timeStop = Instant.ofEpochSecond(timeStart.getEpochSecond() + time);

        return bearer + Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(timeStart))
                .setExpiration(Date.from(timeStop))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Извлекает из запроса токен доступа и удаляет "Bearer_".
     *
     * @param request запрос к серверу
     * @return токен доступа без "Bearer_".
     */
    @NonNull
    @Override
    public String getToken(@NonNull HttpServletRequest request) {
        var headerValue = request.getHeader(headerToken);
        return getToken(headerValue);
    }

    /**
     * Удаляет из токена "Bearer_".
     *
     * @param token токен доступа
     * @return токен доступа без "Bearer_".
     */
    @NonNull
    @Override
    public String getToken(@NonNull String token) {
        return token.replace(bearer, "");
    }

    /**
     * Проверяет время жизни токена.
     *
     * @param token токен доступа
     * @return true если токен действительный иначе false.
     */
    @Override
    public boolean isValidToken(@NonNull String token) {
        var parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();

        try {
            return !parser
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(Date.from(Instant.now()));
        } catch (ExpiredJwtException e) {
            log.error("Время жизни токен истекло");
        }
        return false;
    }

    /**
     * Получает имя пользователя пользователя из токена.
     *
     * @param token токен доступа
     * @return имя пользователя.
     */
    @NonNull
    @Override
    public String getUsername(@NonNull String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
