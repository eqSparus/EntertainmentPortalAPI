package ru.portal.rest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.portal.entities.dto.response.auth.DtoFailedResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Обработчик ошибок.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestControllerAdvice
public class MessageExceptionRest {


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DtoFailedResponse getErrorValidMessage(HttpServletRequest request) {
        return DtoFailedResponse.builder()
                .message("Неверные данные!")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getContextPath() + request.getServletPath())
                .build();
    }

}
