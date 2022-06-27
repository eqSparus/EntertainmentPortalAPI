package ru.portal.security.services.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.portal.security.services.TokenService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

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

    @NonNull
    @Override
    public String createToken(@NonNull String email) {
        return createToken(email, validTime);
    }

    @NonNull
    @Override
    public String createToken(@NonNull String email, @NonNull Long time) {
        var timeStart = Instant.now();
        var timeStop = Instant.ofEpochSecond(timeStart.getEpochSecond() + time);

        return bearer + Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(timeStart))
                .setExpiration(Date.from(timeStop))
                .signWith(secretKey)
                .compact();
    }

    @NonNull
    @Override
    public String getToken(@NonNull HttpServletRequest request) {
        var headerValue = request.getHeader(headerToken);
        return getToken(headerValue);
    }

    @NonNull
    @Override
    public String getToken(@NonNull String token) {
        return token.substring(bearer.length());
    }

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

    @NonNull
    @Override
    public String getEmail(@NonNull String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
