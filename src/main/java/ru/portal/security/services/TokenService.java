package ru.portal.security.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;

public interface TokenService {

    @NonNull
    String createToken(@NonNull String email);

    @NonNull
    String createToken(@NonNull String email, Long time);

    @NonNull
    String getToken(@NonNull HttpServletRequest request);

    @NonNull
    String getToken(@NonNull String token);

    boolean isValidToken(@NonNull String token);

    @NonNull
    String getEmail(@NonNull String token);

}
