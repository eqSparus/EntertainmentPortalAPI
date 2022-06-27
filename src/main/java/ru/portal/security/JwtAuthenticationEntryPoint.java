package ru.portal.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.portal.entities.dto.response.DtoFailedAuthResponse;

import java.io.IOException;
import java.time.OffsetDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    static String MESSAGE = "Для доступа к ресурсу необходима аутентификация";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        var dtoResponse = DtoFailedAuthResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(MESSAGE)
                .path(request.getContextPath() + request.getServletPath())
                .timestamp(OffsetDateTime.now())
                .build();

        mapper.writeValue(response.getOutputStream(), dtoResponse);


    }
}
