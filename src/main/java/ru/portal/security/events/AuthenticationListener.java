package ru.portal.security.events;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import ru.portal.entities.Status;
import ru.portal.entities.auth.ConfirmationToken;
import ru.portal.mail.EmailService;
import ru.portal.repositories.UserRepository;
import ru.portal.repositories.auth.ConfirmationTokenRepository;
import ru.portal.repositories.auth.LoginAttemptRepository;
import ru.portal.security.services.UserService;
import ru.portal.security.utilities.RandomToken;

import java.time.Instant;

/**
 * Слушатели для событий авторизации и регистрации
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
public class AuthenticationListener {

    @Value("${security.confirmation.lifetimeInSecond}")
    Long confirmationTime;

    @Value("${security.blocking.blockingPeriodInSecond}")
    Long blockingPeriod;

    @Value("${security.blocking.maxAttempts}")
    Integer maxAttempt;

    final UserRepository userRepository;
    final LoginAttemptRepository attemptRepository;
    final UserService userService;
    final ConfirmationTokenRepository confirmationTokenRepository;
    final EmailService emailService;

    @Autowired
    public AuthenticationListener(UserRepository userRepository,
                                  LoginAttemptRepository attemptRepository,
                                  UserService userService,
                                  ConfirmationTokenRepository confirmationTokenRepository,
                                  EmailService emailService) {
        this.userRepository = userRepository;
        this.attemptRepository = attemptRepository;
        this.userService = userService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailService = emailService;
    }

    /**
     * При неверных учетных данных, метод исщет пользователя в БД
     * и в случае его сущестования увеличивает счетчик попыток на единицу, если
     * счетчик равен максимальному допустимому значению обнуляет счетчик и блокирует
     * пользователя на заданое время.
     *
     * @param event событие неверных учетных данных пользователя.
     */
    @Transactional
    @EventListener(classes = AuthenticationFailureBadCredentialsEvent.class)
    public void onApplicationFailure(AuthenticationFailureBadCredentialsEvent event) {

        var username = (String) event.getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(username);

        log.info("{}", username);

        user.ifPresent(u -> {

            var attempt = attemptRepository.findByUser(u)
                    .orElseThrow(IllegalArgumentException::new);

            if (attempt.getNumberAttempt().equals(maxAttempt)) {
                attempt.setNumberAttempt(0);
                attempt.setLockTime(Instant.now().plusSeconds(blockingPeriod).toEpochMilli());
                u.setStatus(Status.BLOCK);
                userRepository.save(u);
            } else {
                attempt.setNumberAttempt(attempt.getNumberAttempt() + 1);
            }
            attemptRepository.save(attempt);
        });
    }


    /**
     * При успешной авторизации находит пользователя и его количество
     * попыток входа после чего обнуляет счетчик.
     *
     * @param event событие успешной авторизации пользователя.
     */
    @Transactional
    @EventListener(classes = AuthenticationSuccessEvent.class)
    public void onApplicationSuccess(AuthenticationSuccessEvent event) {

        var userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(IllegalArgumentException::new);

        var attempt = attemptRepository.findByUser(user)
                .orElseThrow(IllegalArgumentException::new);

        attempt.setNumberAttempt(0);
        attemptRepository.save(attempt);
    }

    /**
     * Событие авторизации пользователя, если пользователь существует
     * проверяет заблокирован ли он и вышло ли время блокировки в случее
     * выполнения проверки обновляет статус пользователя на {@link Status#ACTIVE}.
     *
     * @param event событие авторизации пользователя.
     * @see ru.portal.security.events.LoginUserEvent
     */
    @Transactional
    @EventListener(classes = LoginUserEvent.class)
    public void onApplicationLogin(LoginUserEvent event) {

        var request = event.getRequest();

        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    if (userService.isTimeBlock(u)) {
                        userService.updateStatus(Status.ACTIVE, u);
                    }
                });

    }

    /**
     * Создает токен подтверждения аккаунта и сохранят в БД
     * Формирует ссылку на подтверждение регистрации и
     * отправляет ей на почту.
     *
     * @param event событие успешной регистрации пользователя.
     * @see ru.portal.security.events.RegistrationUserEvent
     */
    @Transactional
    @EventListener(classes = RegistrationUserEvent.class)
    public void onApplicationEmail(RegistrationUserEvent event) {
        var user = event.getUser();

        var confirmationToken = ConfirmationToken.builder()
                .token(RandomToken.getToken())
                .lifetime(Instant.now().plusSeconds(confirmationTime).toEpochMilli())
                .user(user)
                .build();

        confirmationTokenRepository.save(confirmationToken);

        var message = emailService.getHtmlMail("registrationUserEmail", () -> {
            var context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("token", confirmationToken.getToken());
            return context;
        });

        emailService.sendEmail(user.getEmail(), "Подтверждение регистрации", message);

    }
}
