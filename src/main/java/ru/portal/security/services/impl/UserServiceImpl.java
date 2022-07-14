package ru.portal.security.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.portal.entities.Role;
import ru.portal.entities.Status;
import ru.portal.entities.User;
import ru.portal.entities.auth.LoginAttempt;
import ru.portal.entities.dto.request.auth.DtoUserRequest;
import ru.portal.entities.dto.response.auth.DtoAuthenticationResponse;
import ru.portal.entities.dto.response.auth.DtoSuccessRegResponse;
import ru.portal.repositories.UserRepository;
import ru.portal.repositories.auth.LoginAttemptRepository;
import ru.portal.security.events.AuthenticationPublisher;
import ru.portal.security.services.TokenRefreshService;
import ru.portal.security.services.TokenService;
import ru.portal.security.services.UserService;
import ru.portal.security.services.exception.IncorrectCredentialsException;
import ru.portal.security.services.exception.UserBannedException;
import ru.portal.security.services.exception.UserExistsException;

import java.time.Instant;

/**
 * Реализация интерфейса {@link UserService} для взаимодействия
 * с пользователем.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    PasswordEncoder encoder;
    LoginAttemptRepository attemptRepository;
    TokenService tokenService;
    TokenRefreshService refreshService;
    AuthenticationManager authenticationManager;
    AuthenticationPublisher authenticationPublisher;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder encoder,
                           LoginAttemptRepository attemptRepository,
                           TokenService tokenService,
                           TokenRefreshService refreshService,
                           AuthenticationManager authenticationManager,
                           AuthenticationPublisher authenticationPublisher) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.attemptRepository = attemptRepository;
        this.tokenService = tokenService;
        this.refreshService = refreshService;
        this.authenticationManager = authenticationManager;
        this.authenticationPublisher = authenticationPublisher;
    }


    /**
     * Регистрирует пользователя в системе, проверяет пользователя на существование.
     * Если пользователя не существует добавляет его в систему и создает сущность подсчет попыток входа
     * При удачной регистрации издает событие {@link ru.portal.security.events.RegistrationUserEvent}
     *
     * @param request тело запроса.
     * @return ответ об успешной аутентификации.
     * @throws UserExistsException бросаеться если пользователь уже существует в системе.
     * @see DtoUserRequest
     * @see DtoSuccessRegResponse
     * @see ru.portal.security.events.RegistrationUserEvent
     */
    @Transactional(rollbackFor = UserExistsException.class)
    @Override
    public DtoSuccessRegResponse registrationUser(@NonNull DtoUserRequest request) throws UserExistsException {

        var user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail());


        if (user.isEmpty()) {

            var newUser = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(encoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .status(Status.AWAIT)
                    .build();
            userRepository.save(newUser);

            var attempt = LoginAttempt.builder()
                    .user(newUser)
                    .build();
            attemptRepository.save(attempt);

            authenticationPublisher.publishEventRegistration(newUser);

            return DtoSuccessRegResponse.builder()
                    .message("Пользователь зарегистрирован проверьте почту!")
                    .status(HttpStatus.CREATED.value())
                    .build();
        } else {
            throw new UserExistsException();
        }

    }

    /**
     * Авторизует пользвотеля в системе, при успешной авторизации создает токен доступа и
     * токен обновления, после чего формирует ответ. До начала авторизации пробрасываеться
     * событие {@link ru.portal.security.events.LoginUserEvent}
     *
     * @param request тело запроса.
     * @return ответ об успешной авторизации.
     * @throws IncorrectCredentialsException бросаеться если данные пользователя неверны.
     * @throws UserBannedException           бросаеться если пользователь заблокирован.
     * @see DtoAuthenticationResponse
     * @see DtoUserRequest
     * @see ru.portal.security.events.LoginUserEvent
     */
    @NonNull
    @Override
    public DtoAuthenticationResponse login(@NonNull DtoUserRequest request)
            throws IncorrectCredentialsException, UserBannedException {

        authenticationPublisher.publishEventLogin(request);

        try {
            var authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            authenticationManager.authenticate(authentication);

            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(IllegalArgumentException::new);

            var token = tokenService.createToken(request.getUsername());
            var refreshToken = refreshService.addRefreshToken(user)
                    .orElseThrow(IllegalArgumentException::new)
                    .getToken();

            return DtoAuthenticationResponse.builder()
                    .authorization(token)
                    .refreshToken(refreshToken)
                    .build();

        } catch (BadCredentialsException e) {
            throw new IncorrectCredentialsException(e);
        } catch (LockedException e) {
            throw new UserBannedException("Пользователь заблокирован на один час!", e);
        }
    }

    /**
     * Проверяет истекло ли время блокировки пользователя.
     *
     * @param user пользователь для проверки.
     * @return true если блокировка закончилась иначе false.
     * @see ru.portal.entities.User
     */
    @Override
    public boolean isTimeBlock(@NonNull User user) {
        var attempt = attemptRepository.findByUser(user)
                .orElseThrow(IllegalArgumentException::new);

        return user.getStatus().equals(Status.BLOCK) &&
                attempt.getLockTime() < Instant.now().toEpochMilli();
    }

    /**
     * Обновления статуса пользователя.
     *
     * @param status стату пользователя.
     * @param user   пользователь для обновления.
     * @see ru.portal.entities.Status
     * @see ru.portal.entities.User
     */
    @Override
    public void updateStatus(@NonNull Status status, @NonNull User user) {
        user.setStatus(status);
        userRepository.save(user);
    }

    /**
     * Выход пользвоателя из системы, удаления токена обновления из хранилища.
     *
     * @param refreshToken токен обновления.
     */
    @Override
    public void logout(@NonNull String refreshToken) {
        refreshService.deleteRefreshToken(refreshToken);
    }

    /**
     * Проверяет существует ли имя пользователя.
     *
     * @param username имя пользователя.
     * @return true если имя существует, false если нет.
     */
    @Override
    public boolean checkUsername(@NonNull String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Проверяет существует ли электронный адрес пользователя.
     *
     * @param email электронная почта пользователя.
     * @return true если адрес существует, false если нет.
     */
    @Override
    public boolean checkEmail(@NonNull String email) {
        return userRepository.existsByEmail(email);
    }
}
