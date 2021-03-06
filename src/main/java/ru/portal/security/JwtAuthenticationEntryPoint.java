package ru.portal.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.portal.entities.dto.response.auth.DtoFailedResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Конечная точка на которую будут перенаправляться неавторизованые запросы
 * Возвращает статус 401 и сообщение об ошибке {@value MESSAGE}
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    static String MESSAGE = "Для доступа к ресурсу необходима аутентификация";

    ObjectMapper mapper;

    @Autowired
    public JwtAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        var dtoResponse = DtoFailedResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(MESSAGE)
                .path(request.getContextPath() + request.getServletPath())
                .build();

        mapper.writeValue(response.getOutputStream(), dtoResponse);
    }
}
