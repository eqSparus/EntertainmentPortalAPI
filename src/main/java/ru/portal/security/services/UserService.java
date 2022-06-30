package ru.portal.security.services;

import org.springframework.lang.NonNull;
import ru.portal.entities.Status;
import ru.portal.entities.User;
import ru.portal.entities.dto.request.DtoUserRequest;
import ru.portal.entities.dto.response.auth.DtoAuthenticationResponse;
import ru.portal.entities.dto.response.auth.DtoSuccessRegResponse;
import ru.portal.security.services.exception.IncorrectCredentialsException;
import ru.portal.security.services.exception.UserBannedException;
import ru.portal.security.services.exception.UserExistsException;

/**
 * Интерфейс предоставляет методы для взаимодействия с пользователем.
 *
 * @author Федорышин К.В.
 */
public interface UserService {

    /**
     * Регистрирует пользователя в системе.
     *
     * @param request тело запроса.
     * @return ответ об успешной аутентификации.
     * @throws UserExistsException бросаеться если пользователь уже существует в системе.
     * @see ru.portal.entities.dto.request.DtoUserRequest
     * @see DtoSuccessRegResponse
     */
    DtoSuccessRegResponse registrationUser(@NonNull DtoUserRequest request) throws UserExistsException;

    /**
     * Авторизация пользователя в системе.
     *
     * @param request тело запроса.
     * @return ответ об успешной авторизации.
     * @throws IncorrectCredentialsException бросаеться если данные пользователя неверны.
     * @throws UserBannedException           бросаеться если пользователь заблокирован.
     * @see DtoAuthenticationResponse
     * @see ru.portal.entities.dto.request.DtoUserRequest
     */
    @NonNull
    DtoAuthenticationResponse login(@NonNull DtoUserRequest request)
            throws IncorrectCredentialsException, UserBannedException;

    /**
     * Проверяет закончилась ли блокировка аккаунта.
     *
     * @param user пользователь для проверки.
     * @return true если блокировка закончилась иначе false.
     * @see ru.portal.entities.User
     */
    boolean isTimeBlock(@NonNull User user);

    /**
     * Обновляет стату пользователя.
     *
     * @param status стату пользователя.
     * @param user   пользователь для обновления.
     * @see ru.portal.entities.Status
     * @see ru.portal.entities.User
     */
    void updateStatus(@NonNull Status status, @NonNull User user);

    /**
     * Выход пользователя из аккаунта.
     *
     * @param refreshToken токен обновления.
     */
    void logout(@NonNull String refreshToken);

    /**
     * Проверяет существует ли имя пользователя.
     *
     * @param username имя пользователя.
     * @return true если имя существует, false если нет.
     */
    boolean checkUsername(@NonNull String username);

    /**
     * Проверяет существует ли электронный адрес пользователя.
     *
     * @param email электронная почта пользователя.
     * @return true если адрес существует, false если нет.
     */
    boolean checkEmail(@NonNull String email);
}
