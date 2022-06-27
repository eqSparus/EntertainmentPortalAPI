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
import ru.portal.entities.dto.request.DtoUserRequest;
import ru.portal.entities.dto.response.DtoAuthenticationResponse;
import ru.portal.entities.dto.response.DtoSuccessAuthResponse;
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
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

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


    @Transactional(rollbackFor = UserExistsException.class)
    @Override
    public DtoSuccessAuthResponse registrationUser(@NonNull DtoUserRequest request) throws UserExistsException {

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

            return DtoSuccessAuthResponse.builder()
                    .userId(newUser.getId())
                    .timestamp(OffsetDateTime.now())
                    .message("Пользователь зарегистрирован прочерьте почту!")
                    .status(HttpStatus.CREATED.value())
                    .build();
        } else {
            throw new UserExistsException();
        }

    }

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
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .createAt(user.getCreateAt())
                    .updateAt(user.getUpdateAt())
                    .authorization(token)
                    .refreshToken(refreshToken)
                    .timestamp(ZonedDateTime.now())
                    .build();

        } catch (BadCredentialsException e) {
            throw new IncorrectCredentialsException(e);
        } catch (LockedException e) {
            throw new UserBannedException(e);
        }
    }

    @Override
    public boolean isTimeBlock(@NonNull User user) throws IncorrectCredentialsException {
        var attempt = attemptRepository.findByUser(user)
                .orElseThrow(IllegalArgumentException::new);

        return user.getStatus().equals(Status.BLOCK) &&
                attempt.getLockTime() < Instant.now().toEpochMilli();
    }

    @Override
    public void updateStatus(@NonNull Status status, @NonNull User user) {
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    public void logout(@NonNull String refreshToken) {
        refreshService.deleteRefreshToken(refreshToken);
    }
}
