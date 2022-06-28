package ru.portal.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.portal.entities.dto.response.DtoFailedResponse;
import ru.portal.security.services.exception.*;

import java.time.OffsetDateTime;

/**
 * Обработчик ошибок.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestControllerAdvice
public class MessageErrorRest {

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler({UserExistsException.class, RefreshTokenNotExistsException.class,
            IncorrectCredentialsException.class, UserBannedException.class, TokenTimeExpiredException.class,
            ConfirmationTokenNotExistException.class})
    public DtoFailedResponse getErrorMessage(Throwable throwable, HttpServletRequest request) {
        return DtoFailedResponse.builder()
                .message(throwable.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(OffsetDateTime.now())
                .path(request.getContextPath() + request.getServletPath())
                .build();
    }

}
