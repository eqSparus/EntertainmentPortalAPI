package ru.portal.rest.auth;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.portal.entities.dto.response.auth.DtoFailedResponse;
import ru.portal.security.services.exception.*;

import javax.servlet.http.HttpServletRequest;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestControllerAdvice
public class HandleExceptionAuthRest {

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler({UserExistsException.class, IncorrectCredentialsException.class, UserBannedException.class})
    public DtoFailedResponse getMessageExAuthentication(Throwable throwable, HttpServletRequest request) {
        return DtoFailedResponse.builder()
                .message(throwable.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .path(request.getContextPath() + request.getServletPath())
                .build();
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({RefreshTokenNotExistsException.class, RefreshTokenTimeUpException.class})
    public DtoFailedResponse getMessageExRefreshToken(HttpServletRequest request) {
        return DtoFailedResponse.builder()
                .message("Ошибка токена обновления")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getContextPath() + request.getServletPath())
                .build();
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({ConfirmationTokenNotExistException.class, ConfirmationTokenTimeExpiredException.class})
    public DtoFailedResponse getMessageExConfirmationToken(HttpServletRequest request) {
        return DtoFailedResponse.builder()
                .message("Ошибка токена подтверждения")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getContextPath() + request.getServletPath())
                .build();
    }



}
